package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.EmployerProfile;
import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.EmployerProfileRepository;
import com.jobportal.jobportal.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employer/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployerProfileController {

    private final EmployerProfileRepository profileRepository;
    private final UserRepository userRepository;

    public EmployerProfileController(
            EmployerProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // ================= GET PROFILE =================

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(
            @PathVariable Long userId,
            Authentication authentication) {

        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        if (!"RECRUITER".equals(user.getRole())) {
            return ResponseEntity.badRequest()
                    .body("User is not a recruiter.");
        }

        if (!user.getEmail().equalsIgnoreCase(
                authentication.getName())) {

            return ResponseEntity
                    .status(403)
                    .body("You are not authorized to view this profile.");
        }

        Optional<EmployerProfile> profile =
                profileRepository.findByUserId(userId);

        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());
        }

        return ResponseEntity.notFound().build();
    }

    // ================= CREATE / UPDATE PROFILE =================

    @PutMapping("/{userId}")
    public ResponseEntity<?> saveProfile(
            @PathVariable Long userId,
            @RequestBody EmployerProfile profileData,
            Authentication authentication) {

        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("User not found.");
        }

        User user = userOptional.get();

        if (!"RECRUITER".equals(user.getRole())) {
            return ResponseEntity
                    .badRequest()
                    .body("Only recruiters can manage employer profiles.");
        }

        if (!user.getEmail().equalsIgnoreCase(
                authentication.getName())) {

            return ResponseEntity
                    .status(403)
                    .body("You are not authorized to update this profile.");
        }

        Optional<EmployerProfile> existingProfile =
                profileRepository.findByUserId(userId);

        EmployerProfile profile;

        if (existingProfile.isPresent()) {

            profile = existingProfile.get();

        } else {

            profile = new EmployerProfile();
            profile.setUser(user);
        }

        profile.setCompanyName(
                profileData.getCompanyName()
        );

        profile.setCompanyDescription(
                profileData.getCompanyDescription()
        );

        profile.setWebsite(
                profileData.getWebsite()
        );

        profile.setLocation(
                profileData.getLocation()
        );

        EmployerProfile saved =
                profileRepository.save(profile);

        return ResponseEntity.ok(saved);
    }
}