package vn.tuanphampp9.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.tuanphampp9.jobhunter.domain.Permission;
import vn.tuanphampp9.jobhunter.domain.Role;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.service.UserService;
import vn.tuanphampp9.jobhunter.util.SecurityUtil;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;
import vn.tuanphampp9.jobhunter.util.error.PermissionException;

//execute after security configuration 
@Transactional // to avoid lazy loading exception(wait for the transaction to be committed)
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByEmail(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(p -> p.getApiPath().equals(path) && p.getMethod().equals(httpMethod));
                    if (!isAllow) {
                        throw new PermissionException("You don't have permission to access this apiPath");
                    }
                } else {
                    throw new PermissionException("You don't have permission to access this apiPath");
                }
            }
        }
        return true;
    }
}
