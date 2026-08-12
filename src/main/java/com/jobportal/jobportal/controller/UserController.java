package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        User registeredUser = userService.registerUser(user);

        if (registeredUser == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already exists"));
        }

        return ResponseEntity.ok(registeredUser);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        try {

            User user = userService.loginUser(email, password);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful",
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}
