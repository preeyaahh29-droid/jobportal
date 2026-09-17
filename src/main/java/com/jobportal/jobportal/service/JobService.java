package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.JobRepository;
import com.jobportal.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL JOBS
    // =========================================================

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // =========================================================
    // GET JOB BY ID
    // =========================================================

    public Job getJobById(Long id) {
        return jobRepository
                .findById(id)
                .orElse(null);
    }

    // =========================================================
    // GET JOBS BY RECRUITER
    // =========================================================

    public List<Job> getJobsByRecruiter(Long recruiterId) {

        return jobRepository
                .findByRecruiterId(recruiterId);
    }

    // =========================================================
    // CREATE JOB
    // =========================================================

    public Job createJob(
            String email,
            Job job) {

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recruiter account not found."
                        )
                );

        // Make sure logged-in user is a recruiter
        if (!"RECRUITER".equals(recruiter.getRole())) {

            throw new IllegalArgumentException(
                    "Only recruiters can post jobs."
            );
        }

        // IMPORTANT:
        // Job belongs to the logged-in recruiter
        job.setRecruiter(recruiter);

        return jobRepository.save(job);
    }

    // =========================================================
    // UPDATE JOB
    // =========================================================

    public Job updateJob(
            Long jobId,
            String email,
            Job jobDetails) {

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recruiter account not found."
                        )
                );

        if (!"RECRUITER".equals(recruiter.getRole())) {

            throw new IllegalArgumentException(
                    "Only recruiters can update jobs."
            );
        }

        Job job = jobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job not found."
                        )
                );

        // Check ownership
        if (job.getRecruiter() == null ||
                !job.getRecruiter()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to update this job."
            );
        }

        job.setTitle(
                jobDetails.getTitle()
        );

        job.setCompany(
                jobDetails.getCompany()
        );

        job.setLocation(
                jobDetails.getLocation()
        );

        job.setDescription(
                jobDetails.getDescription()
        );

        job.setSalary(
                jobDetails.getSalary()
        );

        job.setJobType(
                jobDetails.getJobType()
        );

        job.setExperience(
                jobDetails.getExperience()
        );

        job.setSkills(
                jobDetails.getSkills()
        );

        return jobRepository.save(job);
    }

    // =========================================================
    // DELETE JOB
    // =========================================================

    public void deleteJob(
            Long jobId,
            String email) {

        User recruiter = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recruiter account not found."
                        )
                );

        if (!"RECRUITER".equals(recruiter.getRole())) {

            throw new IllegalArgumentException(
                    "Only recruiters can delete jobs."
            );
        }

        Job job = jobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job not found."
                        )
                );

        // Check ownership
        if (job.getRecruiter() == null ||
                !job.getRecruiter()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to delete this job."
            );
        }

        jobRepository.delete(job);
    }

    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    public List<Job> searchByTitle(
            String title) {

        return jobRepository
                .findByTitleContainingIgnoreCase(title);
    }

    // =========================================================
    // SEARCH BY LOCATION
    // =========================================================

    public List<Job> searchByLocation(
            String location) {

        return jobRepository
                .findByLocationContainingIgnoreCase(location);
    }

    // =========================================================
    // SEARCH BY COMPANY
    // =========================================================

    public List<Job> searchByCompany(
            String company) {

        return jobRepository
                .findByCompanyContainingIgnoreCase(company);
    }

    // =========================================================
    // FILTER BY MINIMUM SALARY
    // =========================================================

    public List<Job> filterBySalary(
        Double minSalary) {

    return jobRepository
            .findBySalaryGreaterThanEqual(minSalary);
}
}