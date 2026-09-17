package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.security.JwtService;
import com.jobportal.jobportal.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Register a new user.
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {

        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Name, email and password are required"));
        }

        // Only the two application roles are accepted.
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("JOB_SEEKER");
        }

        String role = user.getRole().trim().toUpperCase();

        if (!role.equals("JOB_SEEKER") && !role.equals("RECRUITER")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid role"));
        }

        user.setRole(role);

        try {
            User registeredUser = userService.registerUser(user);

            if (registeredUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email already exists"));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Registration successful",
                            "id", registeredUser.getId(),
                            "name", registeredUser.getName(),
                            "email", registeredUser.getEmail(),
                            "role", registeredUser.getRole()
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to register user"));
        }
    }

    // Login and issue a JWT after database credential verification.
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password are required"));
        }

        try {
            User user = userService.loginUser(email.trim(), password);

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful",
                            "token", token,
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    // Get all users.
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get a user by email.
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}
