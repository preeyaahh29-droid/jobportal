package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.UserRepository;
import com.jobportal.jobportal.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Register
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return null;
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        if (user.getRole() == null ||
                user.getRole().isBlank()) {

            user.setRole("JOB_SEEKER");
        }

        return userRepository.save(user);
    }

    // Login
    public User loginUser(
            String email,
            String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    // Generate JWT
    public String generateToken(User user) {

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }
}