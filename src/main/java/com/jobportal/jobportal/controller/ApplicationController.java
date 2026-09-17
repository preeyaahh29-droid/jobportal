package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.Application;
import com.jobportal.jobportal.service.ApplicationService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

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

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    // =========================================================
    // APPLY FOR JOB
    // =========================================================

    @PostMapping(
            value = "/job/{jobId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> applyForJob(
            @PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            Application application =
                    applicationService.applyForJob(
                            email,
                            jobId,
                            resume
                    );

            return ResponseEntity.ok(application);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Failed to submit application."
                    );
        }
    }

    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getAllApplications() {

        return ResponseEntity.ok(
                applicationService.getAllApplications()
        );
    }

    // =========================================================
    // GET MY APPLICATIONS
    // =========================================================

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getApplicationsByEmail(
            @PathVariable String email,
            Authentication authentication) {

        try {

            String loggedInEmail =
                    authentication.getName();

            // Prevent one user from viewing
            // another user's applications
            if (!loggedInEmail.equalsIgnoreCase(email)) {

                return ResponseEntity
                        .status(403)
                        .body(
                                "You are not authorized to view these applications."
                        );
            }

            List<Application> applications =
                    applicationService
                            .getApplicationsByEmail(
                                    loggedInEmail
                            );

            return ResponseEntity.ok(applications);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to load applications."
                    );
        }
    }

    // =========================================================
    // GET APPLICATIONS FOR JOB
    // =========================================================

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsByJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        try {

            String recruiterEmail =
                    authentication.getName();

            List<Application> applications =
                    applicationService
                            .getApplicationsByJob(
                                    jobId,
                                    recruiterEmail
                            );

            return ResponseEntity.ok(applications);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(403)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to load applicants."
                    );
        }
    }

    // =========================================================
    // VIEW RESUME
    // =========================================================

    @GetMapping("/resume/{fileName:.+}")
    public ResponseEntity<Resource> viewResume(
            @PathVariable String fileName) {

        try {

            Path filePath =
                    applicationService
                            .getResumeFile(fileName);

            if (filePath == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {

                contentType =
                        "application/octet-stream";
            }

            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    filePath
                                            .getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // =========================================================
    // UPDATE APPLICATION STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        try {

            String recruiterEmail =
                    authentication.getName();

            Application updatedApplication =
                    applicationService.updateStatus(
                            id,
                            status,
                            recruiterEmail
                    );

            return ResponseEntity.ok(
                    updatedApplication
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to update application status."
                    );
        }
    }

    // =========================================================
    // DELETE / WITHDRAW APPLICATION
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            String email =
                    authentication.getName();

            applicationService.deleteApplication(
                    id,
                    email
            );

            return ResponseEntity.ok(
                    "Application deleted successfully."
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(403)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to delete application."
                    );
        }
    }
}