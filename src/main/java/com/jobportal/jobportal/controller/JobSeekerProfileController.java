package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.JobSeekerProfile;
import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.JobSeekerProfileRepository;
import com.jobportal.jobportal.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/jobseeker/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerProfileController {

    private final JobSeekerProfileRepository profileRepository;
    private final UserRepository userRepository;

    public JobSeekerProfileController(
            JobSeekerProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // ================= GET PROFILE =================

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(
            @PathVariable Long userId) {

        Optional<JobSeekerProfile> profile =
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
            @RequestBody JobSeekerProfile profileData) {

        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("User not found.");
        }

        User user = userOptional.get();

        Optional<JobSeekerProfile> existingProfile =
                profileRepository.findByUserId(userId);

        JobSeekerProfile profile;

        if (existingProfile.isPresent()) {

            profile = existingProfile.get();

        } else {

            profile = new JobSeekerProfile();
            profile.setUser(user);
        }

        // Basic information
        profile.setPhone(profileData.getPhone());
        profile.setLocation(profileData.getLocation());
        profile.setHeadline(profileData.getHeadline());
        profile.setAbout(profileData.getAbout());

        // Job preferences
        profile.setPreferredJobRole(
                profileData.getPreferredJobRole()
        );

        profile.setPreferredLocation(
                profileData.getPreferredLocation()
        );

        profile.setExpectedSalary(
                profileData.getExpectedSalary()
        );

        profile.setJobType(
                profileData.getJobType()
        );

        // Professional information
        profile.setSkills(
                profileData.getSkills()
        );

        profile.setEducation(
                profileData.getEducation()
        );

        profile.setExperience(
                profileData.getExperience()
        );

        profile.setProjects(
                profileData.getProjects()
        );

        JobSeekerProfile saved =
                profileRepository.save(profile);

        return ResponseEntity.ok(saved);
    }
}