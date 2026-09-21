package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.UserRepository;
import com.jobportal.jobportal.security.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldRegisterNewUser() {

        User user = new User();

        user.setEmail("user@test.com");
        user.setPassword("password123");
        user.setRole(null);

        when(userRepository.existsByEmail("user@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        User result =
                userService.registerUser(user);

        assertSame(user, result);
        assertEquals("encoded-password", user.getPassword());
        assertEquals("JOB_SEEKER", user.getRole());

        verify(userRepository).save(user);
    }

    @Test
    void registerUser_shouldReturnNullForExistingEmail() {

        User user = new User();
        user.setEmail("existing@test.com");

        when(userRepository.existsByEmail("existing@test.com"))
                .thenReturn(true);

        User result =
                userService.registerUser(user);

        assertNull(result);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginUser_shouldReturnUserForValidPassword() {

        User user = new User();

        user.setEmail("user@test.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        User result =
                userService.loginUser(
                        "user@test.com",
                        "password123"
                );

        assertSame(user, result);
    }

    @Test
    void loginUser_shouldRejectInvalidPassword() {

        User user = new User();

        user.setPassword("encoded-password");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.loginUser(
                                "user@test.com",
                                "wrong-password"
                        )
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }

    @Test
    void generateToken_shouldReturnJwt() {

        User user = new User();

        user.setEmail("user@test.com");
        user.setRole("JOB_SEEKER");

        when(jwtService.generateToken(
                "user@test.com",
                "JOB_SEEKER"
        )).thenReturn("jwt-token");

        String result =
                userService.generateToken(user);

        assertEquals("jwt-token", result);
    }

    @Test
    void getAllUsers_shouldReturnUsers() {

        User user = new User();

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<User> result =
                userService.getAllUsers();

        assertEquals(1, result.size());
        assertSame(user, result.get(0));
    }

    @Test
    void getUserByEmail_shouldReturnUser() {

        User user = new User();

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        User result =
                userService.getUserByEmail("user@test.com");

        assertSame(user, result);
    }
}