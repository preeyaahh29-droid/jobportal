package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Application;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.User;

import com.jobportal.jobportal.repository.ApplicationRepository;
import com.jobportal.jobportal.repository.JobRepository;
import com.jobportal.jobportal.repository.UserRepository;

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
    private final UserRepository userRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/resumes")
                    .toAbsolutePath()
                    .normalize();

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.applicationRepository =
                applicationRepository;

        this.jobRepository =
                jobRepository;

        this.userRepository =
                userRepository;

        try {

            Files.createDirectories(
                    uploadDirectory
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not create resume upload directory",
                    e
            );
        }
    }

    // =========================================================
    // APPLY FOR JOB
    // =========================================================

    public Application applyForJob(
            String applicantEmail,
            Long jobId,
            MultipartFile resumeFile) {

        // =====================================================
        // FIND APPLICANT FROM JWT EMAIL
        // =====================================================

        User applicant =
                userRepository
                        .findByEmail(applicantEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found."
                                )
                        );

        if (!"JOB_SEEKER".equals(
                applicant.getRole())) {

            throw new IllegalArgumentException(
                    "Only job seekers can apply for jobs."
            );
        }

        // =====================================================
        // FIND JOB
        // =====================================================

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found."
                                )
                        );


        // Prevent duplicate applications
        if (applicationRepository.existsByApplicantIdAndJobId(
                applicant.getId(),
                jobId)) {

        throw new IllegalArgumentException(
            "You have already applied for this job."
        );
        }

        // =====================================================
        // CHECK RESUME
        // =====================================================

        if (resumeFile == null ||
                resumeFile.isEmpty()) {

            throw new IllegalArgumentException(
                    "Resume file is required."
            );
        }

        try {

            String originalFileName =
                    resumeFile.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid resume file."
                );
            }

            // =================================================
            // ALLOWED FILE TYPES
            // =================================================

            String extension = "";

            int dotIndex =
                    originalFileName.lastIndexOf(".");

            if (dotIndex >= 0) {

                extension =
                        originalFileName
                                .substring(dotIndex)
                                .toLowerCase();
            }

            if (!extension.equals(".pdf") &&
                    !extension.equals(".doc") &&
                    !extension.equals(".docx")) {

                throw new IllegalArgumentException(
                        "Only PDF, DOC and DOCX resumes are allowed."
                );
            }

            // =================================================
            // GENERATE SAFE FILE NAME
            // =================================================

            String storedFileName =
                    UUID.randomUUID() +
                            extension;

            Path filePath =
                    uploadDirectory
                            .resolve(storedFileName)
                            .normalize();

            if (!filePath.startsWith(
                    uploadDirectory)) {

                throw new IllegalArgumentException(
                        "Invalid file path."
                );
            }

            // =================================================
            // SAVE RESUME
            // =================================================

            Files.copy(
                    resumeFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // =================================================
            // CREATE APPLICATION
            // =================================================

            Application application =
                    new Application();

            application.setApplicant(
                    applicant
            );

            application.setJob(
                    job
            );

            application.setResumeUrl(
                    storedFileName
            );

            application.setStatus(
                    "APPLIED"
            );

            return applicationRepository.save(
                    application
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save resume.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    public List<Application> getAllApplications() {

        return applicationRepository.findAll();
    }

    // =========================================================
    // GET APPLICATIONS BY EMAIL
    // =========================================================

    public List<Application> getApplicationsByEmail(
            String email) {

        return applicationRepository
                .findByApplicantEmail(email);
    }

    // =========================================================
    // GET APPLICATIONS BY JOB
    // =========================================================

    public List<Application> getApplicationsByJob(
            Long jobId,
            String recruiterEmail) {

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found."
                                )
                        );


        User recruiter =
                userRepository
                        .findByEmail(recruiterEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recruiter not found."
                                )
                        );

        if (!"RECRUITER".equals(
                recruiter.getRole())) {

            throw new IllegalArgumentException(
                    "Only recruiters can view applicants."
            );
        }

        // =====================================================
        // OWNERSHIP CHECK
        // =====================================================

        if (job.getRecruiter() == null ||
                !job.getRecruiter()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to view applicants for this job."
            );
        }

        return applicationRepository
                .findByJobId(jobId);
    }

    // =========================================================
    // UPDATE APPLICATION STATUS
    // =========================================================

    public Application updateStatus(
            Long id,
            String status,
            String recruiterEmail) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Application not found."
                                )
                        );

        // =====================================================
        // FIND RECRUITER
        // =====================================================

        User recruiter =
                userRepository
                        .findByEmail(recruiterEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recruiter not found."
                                )
                        );

        if (!"RECRUITER".equals(
                recruiter.getRole())) {

            throw new IllegalArgumentException(
                    "Only recruiters can update application status."
            );
        }

        // =====================================================
        // VERIFY JOB OWNERSHIP
        // =====================================================

        Job job = application.getJob();

        if (job == null ||
                job.getRecruiter() == null ||
                !job.getRecruiter()
                        .getId()
                        .equals(recruiter.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to update this application."
            );
        }



        // =====================================================
        // VALIDATE STATUS
        // =====================================================

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Status is required."
            );
        }

        status =
                status
                        .trim()
                        .toUpperCase()
                        .replace(" ", "_");

        if (!status.equals("APPLIED") &&
                !status.equals("VIEWED") &&
                !status.equals("SHORTLISTED") &&
                !status.equals("INTERVIEW") &&
                !status.equals("SELECTED") &&
                !status.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Invalid status. Use APPLIED, VIEWED, " +
                    "SHORTLISTED, INTERVIEW, SELECTED or REJECTED."
            );
        }

        // =====================================================
// PREVENT INVALID STATUS CHANGES
// =====================================================

String currentStatus = application.getStatus();

if (currentStatus == null) {
    currentStatus = "APPLIED";
}

// Once rejected or selected, application is finalized
if (currentStatus.equals("REJECTED") ||
        currentStatus.equals("SELECTED")) {

    throw new IllegalArgumentException(
            "This application is already finalized and cannot be changed."
    );
}

// Prevent moving backwards
if (currentStatus.equals("APPLIED") &&
        !(status.equals("VIEWED") ||
                status.equals("SHORTLISTED") ||
                status.equals("INTERVIEW") ||
                status.equals("SELECTED") ||
                status.equals("REJECTED"))) {

    throw new IllegalArgumentException(
            "Invalid status transition from APPLIED."
    );
}

if (currentStatus.equals("VIEWED") &&
        status.equals("APPLIED")) {

    throw new IllegalArgumentException(
            "Application status cannot move backwards."
    );
}

if (currentStatus.equals("SHORTLISTED") &&
        (status.equals("APPLIED") ||
                status.equals("VIEWED"))) {

    throw new IllegalArgumentException(
            "Application status cannot move backwards."
    );
}

if (currentStatus.equals("INTERVIEW") &&
        (status.equals("APPLIED") ||
                status.equals("VIEWED") ||
                status.equals("SHORTLISTED"))) {

    throw new IllegalArgumentException(
            "Application status cannot move backwards."
    );
}

application.setStatus(status);

return applicationRepository.save(
        application
);
            }

    // =========================================================
    // DELETE / WITHDRAW APPLICATION
    // =========================================================

    public void deleteApplication(
            Long id,
            String applicantEmail) {

        Application application =
                applicationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Application not found."
                                )
                        );

        // =====================================================
        // VERIFY OWNER
        // =====================================================

        if (application.getApplicant() == null ||
                !application
                        .getApplicant()
                        .getEmail()
                        .equalsIgnoreCase(
                                applicantEmail
                        )) {

            throw new IllegalArgumentException(
                    "You are not authorized to withdraw this application."
            );
        }

        // =====================================================
        // DELETE RESUME
        // =====================================================

        if (application.getResumeUrl() != null) {

            try {

                Path filePath =
                        uploadDirectory
                                .resolve(
                                        application
                                                .getResumeUrl()
                                )
                                .normalize();

                if (filePath.startsWith(
                        uploadDirectory)) {

                    Files.deleteIfExists(
                            filePath
                    );
                }

            } catch (IOException e) {

                System.out.println(
                        "Could not delete resume file: "
                                + e.getMessage()
                );
            }
        }

        applicationRepository.delete(
                application
        );
    }

    // =========================================================
// GET RESUME FILE
// =========================================================

public Path getResumeFile(
        String fileName,
        String requesterEmail) {

    Application application =
            applicationRepository
                    .findByResumeUrl(fileName)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Resume not found."
                            )
                    );

    boolean isApplicant =
            application.getApplicant() != null &&
            application.getApplicant()
                    .getEmail()
                    .equalsIgnoreCase(requesterEmail);

    boolean isRecruiter =
            application.getJob() != null &&
            application.getJob().getRecruiter() != null &&
            application.getJob().getRecruiter()
                    .getEmail()
                    .equalsIgnoreCase(requesterEmail);

    if (!isApplicant && !isRecruiter) {

        throw new IllegalArgumentException(
                "You are not authorized to view this resume."
        );
    }

    Path filePath =
            uploadDirectory
                    .resolve(fileName)
                    .normalize();

    if (!filePath.startsWith(uploadDirectory)) {

        throw new IllegalArgumentException(
                "Invalid file name."
        );
    }

    if (!Files.exists(filePath)) {
        return null;
    }

    return filePath;
}
}