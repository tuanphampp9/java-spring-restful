package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Object;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.RestResponse;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.DTO.ResCreateUserDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResUpdateDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResUserDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.UserService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {
        boolean isExist = this.userService.handleCheckUserExist(user.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email is already exist");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User newUser = this.userService.handleCreateUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id)
            throws IdInvalidException {
        User findUser = this.userService.handleGetUser(id);
        if (findUser == null) {
            throw new IdInvalidException("User is not exist");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") Long id)
            throws IdInvalidException {
        User user = this.userService.handleGetUser(id);
        if (user == null) {
            throw new IdInvalidException("User is not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleGetAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateDTO> updateUser(@RequestBody User user)
            throws IdInvalidException {
        User findUser = this.userService.handleGetUser(user.getId());
        if (findUser == null) {
            throw new IdInvalidException("User is not exist");
        }
        findUser.setAddress(user.getAddress());
        findUser.setAge(user.getAge());
        findUser.setEmail(user.getEmail());
        findUser.setGender(user.getGender());
        findUser.setName(user.getName());
        User userResult = this.userService.handleUpdateUser(findUser);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateDTO(userResult));
    }

}
