package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.UserRepository;
import com.jobportal.jobportal.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationControllerTest {

    private RecommendationService recommendationService;
    private UserRepository userRepository;
    private RecommendationController controller;

    @BeforeEach
    void setUp() {
        recommendationService = mock(RecommendationService.class);
        userRepository = mock(UserRepository.class);

        controller = new RecommendationController(
                recommendationService,
                userRepository
        );
    }

    @Test
    void allowsUserToGetOwnRecommendations() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);

        when(userRepository.findByEmail("seeker@example.com"))
                .thenReturn(Optional.of(user));

        List<com.jobportal.jobportal.dto.JobRecommendation>
                recommendations = List.of();

        when(recommendationService.getRecommendations(7L))
                .thenReturn(recommendations);

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "seeker@example.com", null
                );

        var result = controller.getRecommendations(7L, authentication);

        assertSame(recommendations, result);
        verify(recommendationService).getRecommendations(7L);
    }

    @Test
    void rejectsRequestsForAnotherUsersRecommendations() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);

        when(userRepository.findByEmail("seeker@example.com"))
                .thenReturn(Optional.of(user));

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "seeker@example.com", null
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getRecommendations(8L, authentication)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(recommendationService, never()).getRecommendations(anyLong());
    }
}