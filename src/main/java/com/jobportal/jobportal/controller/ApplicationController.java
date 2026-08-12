package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.Application;
import com.jobportal.jobportal.service.ApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Apply for a job with resume
    @PostMapping(
            value = "/job/{jobId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Application applyForJob(
            @PathVariable Long jobId,
            @RequestParam("applicantName") String applicantName,
            @RequestParam("applicantEmail") String applicantEmail,
            @RequestParam("resume") MultipartFile resume) {

        return applicationService.applyForJob(
                jobId,
                applicantName,
                applicantEmail,
                resume
        );
    }

    // Get all applications
    @GetMapping
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    // Get applications by email
    @GetMapping("/email/{email}")
    public List<Application> getApplicationsByEmail(
            @PathVariable String email) {

        return applicationService.getApplicationsByEmail(email);
    }

    // Get applications for a specific job
    @GetMapping("/job/{jobId}")
    public List<Application> getApplicationsByJob(
            @PathVariable Long jobId) {

        return applicationService.getApplicationsByJob(jobId);
    }

    // View / open resume
    @GetMapping("/resume/{fileName:.+}")
    public ResponseEntity<Resource> viewResume(
            @PathVariable String fileName) {

        try {

            Path filePath =
                    applicationService.getResumeFile(fileName);

            if (filePath == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(contentType)
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    filePath.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // Update application status
    @PutMapping("/{id}/status")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return applicationService.updateStatus(id, status);
    }

    // Delete application
    @DeleteMapping("/{id}")
    public String deleteApplication(
            @PathVariable Long id) {

        applicationService.deleteApplication(id);

        return "Application deleted successfully";
    }
}