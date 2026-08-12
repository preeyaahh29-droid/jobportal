package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Application;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.repository.ApplicationRepository;
import com.jobportal.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/resumes").toAbsolutePath().normalize();

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create resume upload directory", e);
        }
    }

    // Apply for a job and upload resume
    public Application applyForJob(
            Long jobId,
            String applicantName,
            String applicantEmail,
            MultipartFile resumeFile) {

        Job job = jobRepository
                .findById(jobId)
                .orElse(null);

        if (job == null) {
            return null;
        }

        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }

        try {
            String originalFileName = resumeFile.getOriginalFilename();

            if (originalFileName == null || originalFileName.isBlank()) {
                throw new IllegalArgumentException("Invalid resume file");
            }

            String extension = "";

            int dotIndex = originalFileName.lastIndexOf(".");

            if (dotIndex >= 0) {
                extension = originalFileName.substring(dotIndex);
            }

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    uploadDirectory.resolve(storedFileName).normalize();

            if (!filePath.startsWith(uploadDirectory)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            Files.copy(
                    resumeFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Application application = new Application();

            application.setApplicantName(applicantName);
            application.setApplicantEmail(applicantEmail);

            // Store the actual uploaded file name
            application.setResumeUrl(storedFileName);

            application.setStatus("APPLIED");
            application.setJob(job);

            return applicationRepository.save(application);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume", e);
        }
    }

    // Get all applications
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get applications by email
    public List<Application> getApplicationsByEmail(String email) {
        return applicationRepository.findByApplicantEmail(email);
    }

    // Get applications by job
    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // Update application status
    public Application updateStatus(Long id, String status) {

        Application application = applicationRepository
                .findById(id)
                .orElse(null);

        if (application == null) {
            return null;
        }

        status = status.toUpperCase();

        if (!status.equals("APPLIED") &&
                !status.equals("SHORTLISTED") &&
                !status.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Invalid status. Use APPLIED, SHORTLISTED or REJECTED."
            );
        }

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    // Delete application
    public void deleteApplication(Long id) {

        Application application = applicationRepository
                .findById(id)
                .orElse(null);

        if (application != null) {

            // Delete uploaded resume
            if (application.getResumeUrl() != null) {

                try {
                    Path filePath =
                            uploadDirectory
                                    .resolve(application.getResumeUrl())
                                    .normalize();

                    if (filePath.startsWith(uploadDirectory)) {
                        Files.deleteIfExists(filePath);
                    }

                } catch (IOException e) {
                    System.out.println(
                            "Could not delete resume file: "
                                    + e.getMessage()
                    );
                }
            }

            applicationRepository.deleteById(id);
        }
    }

    // Get resume file
    public Path getResumeFile(String fileName) {

        Path filePath =
                uploadDirectory.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (!Files.exists(filePath)) {
            return null;
        }

        return filePath;
    }
}