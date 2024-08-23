package vn.tuanphampp9.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.Request.ReqLoginDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResLoginDTO;
import vn.tuanphampp9.jobhunter.service.UserService;
import vn.tuanphampp9.jobhunter.util.SecurityUtil;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${tuanpp9.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // authentication user => need write method loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set authentication into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User user = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName());
        resLoginDTO.setUser(userLogin);

        // create token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());
        resLoginDTO.setAccessToken(accessToken);
        // config refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // update refresh token to user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // set cookie to response(refresh token)
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User userCurrentDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (userCurrentDB != null) {
            userLogin.setId(userCurrentDB.getId());
            userLogin.setEmail(userCurrentDB.getEmail());
            userLogin.setName(userCurrentDB.getName());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "") String refresh_token)
            throws IdInvalidException {
        // check refresh token empty
        if (refresh_token.isEmpty()) {
            throw new IdInvalidException("Refresh token is empty");
        }
        // check valid refresh token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token and email
        User user = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (user == null) {
            throw new IdInvalidException("Refresh token is invalid");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName());
        resLoginDTO.setUser(userLogin);

        // create token
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUser());
        resLoginDTO.setAccessToken(accessToken);
        // config refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update refresh token to user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookie to response(refresh token)
        ResponseCookie cookie = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout()
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = this.userService.handleGetUserByUsername(email);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }
        user.setRefreshToken(null);
        this.userService.handleUpdateUser(user);
        ResponseCookie deleteCookieToken = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookieToken.toString())
                .build();
    }
}
