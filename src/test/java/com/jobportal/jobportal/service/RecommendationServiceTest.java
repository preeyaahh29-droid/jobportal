package com.jobportal.jobportal.service;

import com.jobportal.jobportal.dto.JobRecommendation;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.JobSeekerProfile;
import com.jobportal.jobportal.repository.JobRepository;
import com.jobportal.jobportal.repository.JobSeekerProfileRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_shouldRankBestMatchingJobFirst() {

        JobSeekerProfile profile = new JobSeekerProfile();

        profile.setSkills("Java, SQL, Spring Boot");
        profile.setPreferredJobRole("Java Developer");
        profile.setPreferredLocation("Chennai");
        profile.setJobType("Full Time");
        profile.setExpectedSalary(500000.0);

        Job bestJob = new Job();
        bestJob.setTitle("Java Developer");
        bestJob.setCompany("TCS");
        bestJob.setLocation("Chennai");
        bestJob.setDescription("Java backend development");
        bestJob.setSalary(600000.0);
        bestJob.setJobType("Full Time");
        bestJob.setExperience("0-2 Years");
        bestJob.setSkills("Java, SQL, Spring Boot");

        Job weakerJob = new Job();
        weakerJob.setTitle("Python Developer");
        weakerJob.setCompany("Infosys");
        weakerJob.setLocation("Bangalore");
        weakerJob.setDescription("Python development");
        weakerJob.setSalary(400000.0);
        weakerJob.setJobType("Full Time");
        weakerJob.setExperience("0-2 Years");
        weakerJob.setSkills("Python, Django");

        when(jobSeekerProfileRepository.findByUserId(1L))
                .thenReturn(Optional.of(profile));

        when(jobRepository.findAll())
                .thenReturn(List.of(weakerJob, bestJob));

        List<JobRecommendation> result =
                recommendationService.getRecommendations(1L);

        assertEquals(2, result.size());

        assertEquals(
                "Java Developer",
                result.get(0).getTitle()
        );

        assertTrue(
                result.get(0).getMatchScore()
                        > result.get(1).getMatchScore()
        );

        assertEquals(
                "Excellent match",
                result.get(0).getMatchLabel()
        );

        assertEquals(
                List.of("Java", "SQL", "Spring Boot"),
                result.get(0).getMatchedSkills()
        );

        verify(jobRepository).findAll();
    }

    @Test
    void getRecommendations_shouldRejectMissingProfile() {

        when(jobSeekerProfileRepository.findByUserId(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> recommendationService
                                .getRecommendations(99L)
                );

        assertEquals(
                "Job seeker profile not found.",
                exception.getMessage()
        );

        verify(jobRepository, never()).findAll();
    }
}