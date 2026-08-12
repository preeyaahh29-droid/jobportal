package com.jobportal.jobportal.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin
public class ResumeController {

    private final Path uploadDirectory =
            Paths.get("uploads").toAbsolutePath().normalize();

    public ResumeController() throws IOException {
        Files.createDirectories(uploadDirectory);
    }

    // Upload resume
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select a resume file.");
            }

            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Invalid file name.");
            }

            String extension = "";

            int lastDot = originalFileName.lastIndexOf(".");

            if (lastDot >= 0) {
                extension = originalFileName.substring(lastDot);
            }

            if (!extension.equalsIgnoreCase(".pdf") &&
                    !extension.equalsIgnoreCase(".doc") &&
                    !extension.equalsIgnoreCase(".docx")) {

                return ResponseEntity.badRequest()
                        .body("Only PDF, DOC and DOCX files are allowed.");
            }

            String fileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    uploadDirectory.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String fileUrl =
                    "/api/resumes/view/" + fileName;

            return ResponseEntity.ok(
                    java.util.Map.of(
                            "message", "Resume uploaded successfully",
                            "fileName", fileName,
                            "resumeUrl", fileUrl
                    )
            );

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Failed to upload resume.");
        }
    }

    // View / download resume
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewResume(
            @PathVariable String fileName) {

        try {

            Path filePath =
                    uploadDirectory.resolve(fileName)
                            .normalize();

            if (!filePath.startsWith(uploadDirectory)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() ||
                    !resource.isReadable()) {

                return ResponseEntity.notFound().build();
            }

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {
                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(contentType)
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() +
                                    "\""
                    )
                    .body(resource);

        } catch (MalformedURLException e) {

            return ResponseEntity.internalServerError()
                    .build();

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}
