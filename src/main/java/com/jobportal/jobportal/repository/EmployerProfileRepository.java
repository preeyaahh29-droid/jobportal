package com.jobportal.jobportal.repository;

import com.jobportal.jobportal.entity.EmployerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerProfileRepository
        extends JpaRepository<EmployerProfile, Long> {

    Optional<EmployerProfile> findByUserId(Long userId);
}