package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.dto.JobRecommendation;
import com.jobportal.jobportal.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for smart job recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Creates the recommendation controller.
     *
     * @param recommendationService service for generating recommendations
     */
    public RecommendationController(
            RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Returns ranked job recommendations for a job seeker.
     *
     * @param userId job seeker's user ID
     * @return top five recommended jobs
     */
    @GetMapping("/{userId}")
    public List<JobRecommendation> getRecommendations(
            @PathVariable Long userId) {

        return recommendationService.getRecommendations(userId);
    }
}