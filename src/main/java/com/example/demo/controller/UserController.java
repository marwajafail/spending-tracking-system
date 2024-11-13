package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.UserDto;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.SpLog;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "User Controller", description = "Manage users")
public class UserController {

    private final UserService userService;
    private final LogService logService;

    @Autowired
    public UserController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "This endpoint allows you to create a new user account.")
    public ResponseEntity<GenericDao<UserDto>> createUser(@RequestBody UserDto user){
        try {
            GenericDao<UserDto> genericDao = userService.createUser(user);
            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), null));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "This endpoint allows a user to authenticate and log in to the system.")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            return userService.loginUser(loginRequest);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), null));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify a user", description = "This endpoint allows a user to verify their account by entering a verification code.")
    public ResponseEntity<?> verifyUser(@Param("code") String code) {
        try {
            if (userService.verify(code)) {
                return ResponseEntity.ok("User verified Successfully!");
            } else {
                return ResponseEntity.status(403).body(new LoginResponse("Code Verification is wrong. please provide the correct one."));
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), null));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/request-password-reset")
    @Operation(summary = "Request password reset", description = "This endpoint allows a user to request a password reset by providing their email address.")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        try {
            boolean result = userService.requestPasswordReset(email);
            if (result) {
                return ResponseEntity.ok("Password reset code sent.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist.");
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), null));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "This endpoint allows a user to reset their password using a provided token and new password.")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean result = userService.resetPassword(token, newPassword);
            if (result) {
                return ResponseEntity.ok("Password has been successfully reset.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), null));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "This endpoint allows a user to change their password by providing their old and new passwords.")
    public ResponseEntity<?> changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            boolean result = userService.changePassword(oldPassword, newPassword);
            if (result) {
                return ResponseEntity.ok("Password has been successfully changed.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate a user", description = "This endpoint allows an admin to deactivate a user account.")
    public ResponseEntity<?> deactivateUser(@RequestParam Long uid) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                GenericDao<Boolean> result = userService.deleteUser(uid);
                if (result.getObject()) {
                    return ResponseEntity.ok("User has been deactivated successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrors());
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "This endpoint allows an admin to retrieve a list of all registered users.")
    public ResponseEntity<GenericDao<List<UserDto>>> getAllUsers() {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                GenericDao<List<UserDto>> genericDao = new GenericDao<>();
                genericDao.setObject(userService.getAll(false));
                return genericDao.getErrors().isEmpty() ?
                        new ResponseEntity<>(genericDao, HttpStatus.OK) :
                        new ResponseEntity<>(genericDao, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

