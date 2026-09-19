package com.jobportal.jobportal.repository;

import com.jobportal.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByApplicantEmail(String applicantEmail);

    List<Application> findByJobId(Long jobId);

    boolean existsByApplicantIdAndJobId(
            Long applicantId,
            Long jobId
    );

    Optional<Application> findByResumeUrl(String resumeUrl);
}