package com.jobportal.jobportal.service;

import com.jobportal.jobportal.dto.JobRecommendation;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.JobSeekerProfile;
import com.jobportal.jobportal.repository.JobRepository;
import com.jobportal.jobportal.repository.JobSeekerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Provides smart job recommendations for job seekers.
 */
@Service
public class RecommendationService {

    private final JobRepository jobRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    /**
     * Creates the recommendation service.
     *
     * @param jobRepository repository used to load available jobs
     * @param jobSeekerProfileRepository repository used to load seeker profiles
     */
    public RecommendationService(
            JobRepository jobRepository,
            JobSeekerProfileRepository jobSeekerProfileRepository) {

        this.jobRepository = jobRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }

    /**
     * Generates ranked job recommendations for a job seeker.
     *
     * @param userId logged-in job seeker's user ID
     * @return top five jobs ranked by match score
     */
    public List<JobRecommendation> getRecommendations(Long userId) {

        JobSeekerProfile profile = jobSeekerProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job seeker profile not found."
                        ));

        return jobRepository.findAll()
                .stream()
                .map(job -> calculateRecommendation(job, profile))
                .sorted(Comparator.comparingInt(
                        JobRecommendation::getMatchScore
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the recommendation score for one job.
     *
     * @param job job posting
     * @param profile job seeker profile
     * @return recommendation containing score and skill details
     */
    private JobRecommendation calculateRecommendation(
            Job job,
            JobSeekerProfile profile) {

        List<String> candidateSkills =
                splitValues(profile.getSkills());

        List<String> requiredSkills =
                splitValues(job.getSkills());

        List<String> matchedSkills = new ArrayList<>();

        for (String requiredSkill : requiredSkills) {

            boolean matched = candidateSkills.stream()
                    .anyMatch(candidateSkill ->
                            skillMatches(
                                    candidateSkill,
                                    requiredSkill
                            ));

            if (matched) {
                matchedSkills.add(requiredSkill);
            }
        }

        List<String> missingSkills = requiredSkills.stream()
                .filter(skill -> !matchedSkills.contains(skill))
                .collect(Collectors.toList());

        int skillScore = requiredSkills.isEmpty()
                ? 30
                : (int) Math.round(
                        ((double) matchedSkills.size()
                                / requiredSkills.size()) * 60
                );

        int roleScore = calculateRoleScore(
                profile.getPreferredJobRole(),
                job.getTitle(),
                job.getDescription()
        );

        int locationScore = calculateLocationScore(
                profile.getPreferredLocation(),
                job.getLocation()
        );

        int jobTypeScore = calculateJobTypeScore(
                profile.getJobType(),
                job.getJobType()
        );

        int salaryScore = calculateSalaryScore(
                profile.getExpectedSalary(),
                job.getSalary()
        );

        int totalScore = Math.min(
                100,
                skillScore
                        + roleScore
                        + locationScore
                        + jobTypeScore
                        + salaryScore
        );

        String label = getMatchLabel(totalScore);

        return new JobRecommendation(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getSalary(),
                job.getJobType(),
                job.getSkills(),
                totalScore,
                label,
                matchedSkills,
                missingSkills
        );
    }

    private int calculateRoleScore(
            String preferredRole,
            String title,
            String description) {

        if (isBlank(preferredRole)) {
            return 8;
        }

        String role = preferredRole.toLowerCase(Locale.ROOT);

        String jobTitle = safeLower(title);
        String jobDescription = safeLower(description);

        if (jobTitle.contains(role)
                || role.contains(jobTitle)
                || jobDescription.contains(role)) {
            return 15;
        }

        return 0;
    }

    private int calculateLocationScore(
            String preferredLocation,
            String jobLocation) {

        if (isBlank(preferredLocation)) {
            return 5;
        }

        String preferred =
                preferredLocation.toLowerCase(Locale.ROOT).trim();

        String location =
                safeLower(jobLocation);

        if (location.contains(preferred)
                || preferred.contains(location)) {
            return 10;
        }

        return 0;
    }

    private int calculateJobTypeScore(
            String preferredJobType,
            String jobType) {

        if (isBlank(preferredJobType)
                || isBlank(jobType)) {
            return 3;
        }

        return preferredJobType.trim()
                .equalsIgnoreCase(jobType.trim())
                ? 5
                : 0;
    }

    private int calculateSalaryScore(
            Double expectedSalary,
            Double jobSalary) {

        if (expectedSalary == null
                || expectedSalary <= 0
                || jobSalary == null) {
            return 5;
        }

        return jobSalary >= expectedSalary ? 10 : 0;
    }

    private boolean skillMatches(
            String candidateSkill,
            String requiredSkill) {

        String candidate = candidateSkill
                .toLowerCase(Locale.ROOT)
                .trim();

        String required = requiredSkill
                .toLowerCase(Locale.ROOT)
                .trim();

        if (candidate.isEmpty() || required.isEmpty()) {
            return false;
        }

        if (candidate.equals(required)
                || candidate.contains(required)
                || required.contains(candidate)) {
            return true;
        }

        List<String> candidateWords =
                normalizeWords(candidate);

        List<String> requiredWords =
                normalizeWords(required);

        return requiredWords.stream()
                .allMatch(candidateWords::contains);
    }

    private List<String> splitValues(String value) {

        if (isBlank(value)) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> normalizeWords(String value) {

        return Arrays.stream(
                        value.replaceAll(
                                "[^a-z0-9+#.\\s-]",
                                " "
                        ).split("[\\s,|/]+")
                )
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    private String getMatchLabel(int score) {

        if (score >= 80) {
            return "Excellent match";
        }

        if (score >= 60) {
            return "Good match";
        }

        if (score >= 40) {
            return "Partial match";
        }

        return "Low match";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safeLower(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT).trim();
    }
}