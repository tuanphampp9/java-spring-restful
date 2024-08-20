package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.service.UserService;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/users/create")
    public ResponseEntity<User> createNewUser (@RequestBody User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id)
    throws IdInvalidException
    {
        if(id>1500){
            throw new IdInvalidException("Id is invalid");
        }
        User findUser = this.userService.handleGetUser(id);
        if(findUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete user successfully");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUser(id);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> listUsers = this.userService.handleGetAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(listUsers);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id,@RequestBody User user) {
        User findUser = this.userService.handleGetUser(id);
        if(findUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        user.setId(id);
        User userResult = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userResult);
    }
    
}
