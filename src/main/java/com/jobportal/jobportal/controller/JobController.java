package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =========================================================
    // GET ALL JOBS
    // =========================================================

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    // =========================================================
    // GET JOB BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    // =========================================================
    // GET JOBS BY RECRUITER
    // =========================================================

    @GetMapping("/recruiter/{recruiterId}")
    public List<Job> getJobsByRecruiter(
            @PathVariable Long recruiterId) {

        return jobService.getJobsByRecruiter(recruiterId);
    }

    // =========================================================
    // CREATE JOB
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createJob(
            Authentication authentication,
            @Valid @RequestBody Job job) {

        try {

            String email = authentication.getName();

            Job createdJob =
                    jobService.createJob(email, job);

            return ResponseEntity.ok(createdJob);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // UPDATE JOB
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody Job job) {

        try {

            String email = authentication.getName();

            Job updatedJob =
                    jobService.updateJob(
                            id,
                            email,
                            job
                    );

            return ResponseEntity.ok(updatedJob);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // DELETE JOB
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            jobService.deleteJob(
                    id,
                    email
            );

            return ResponseEntity.ok(
                    "Job deleted successfully"
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // SEARCH BY TITLE
    // =========================================================

    @GetMapping("/search/title")
    public List<Job> searchByTitle(
            @RequestParam String title) {

        return jobService.searchByTitle(title);
    }

    // =========================================================
    // SEARCH BY LOCATION
    // =========================================================

    @GetMapping("/search/location")
    public List<Job> searchByLocation(
            @RequestParam String location) {

        return jobService.searchByLocation(location);
    }

    // =========================================================
    // SEARCH BY COMPANY
    // =========================================================

    @GetMapping("/search/company")
    public List<Job> searchByCompany(
            @RequestParam String company) {

        return jobService.searchByCompany(company);
    }

    // =========================================================
    // FILTER BY MINIMUM SALARY
    // =========================================================

    @GetMapping("/filter/salary")
    public List<Job> filterBySalary(
        @RequestParam Double minSalary) {
            return jobService.filterBySalary(minSalary);
        }
}