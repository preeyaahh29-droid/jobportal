
package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.dto.JobRecommendation;
import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.UserRepository;
import com.jobportal.jobportal.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for smart job recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    /**
     * Creates the recommendation controller.
     *
     * @param recommendationService service for generating recommendations
     * @param userRepository repository for looking up users
     */
    public RecommendationController(
            RecommendationService recommendationService,
            UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    /**
     * Returns ranked recommendations for the authenticated job seeker.
     *
     * @param userId requested job seeker's user ID
     * @param authentication authenticated security context
     * @return top five recommended jobs
     */
    @GetMapping("/{userId}")
    public List<JobRecommendation> getRecommendations(
            @PathVariable Long userId,
            Authentication authentication) {

        User authenticatedUser = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        if (!authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own recommendations"
            );
        }

        return recommendationService.getRecommendations(userId);
    }
}