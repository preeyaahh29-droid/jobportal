package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Application;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.User;
import com.jobportal.jobportal.repository.ApplicationRepository;
import com.jobportal.jobportal.repository.JobRepository;
import com.jobportal.jobportal.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void updateStatus_shouldUpdateStatusSuccessfully() {

        User recruiter = mock(User.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getRole()).thenReturn("RECRUITER");

        Job job = new Job();
        job.setRecruiter(recruiter);

        Application application = new Application();
        application.setJob(job);
        application.setStatus("APPLIED");

        when(applicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(userRepository.findByEmail("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        when(applicationRepository.save(application))
                .thenReturn(application);

        Application result =
                applicationService.updateStatus(
                        10L,
                        "shortlisted",
                        "recruiter@test.com"
                );

        assertEquals("SHORTLISTED", result.getStatus());

        verify(applicationRepository).save(application);
    }

    @Test
    void updateStatus_shouldRejectNonRecruiter() {

        User user = mock(User.class);

        when(user.getRole()).thenReturn("JOB_SEEKER");

        Application application = new Application();

        when(applicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService.updateStatus(
                                10L,
                                "SHORTLISTED",
                                "seeker@test.com"
                        )
                );

        assertEquals(
                "Only recruiters can update application status.",
                exception.getMessage()
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void updateStatus_shouldRejectUnauthorizedRecruiter() {

        User owner = mock(User.class);

        when(owner.getId()).thenReturn(1L);

        User otherRecruiter = mock(User.class);

        when(otherRecruiter.getId()).thenReturn(2L);
        when(otherRecruiter.getRole()).thenReturn("RECRUITER");

        Job job = new Job();
        job.setRecruiter(owner);

        Application application = new Application();
        application.setJob(job);
        application.setStatus("APPLIED");

        when(applicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(userRepository.findByEmail("other@test.com"))
                .thenReturn(Optional.of(otherRecruiter));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService.updateStatus(
                                10L,
                                "SHORTLISTED",
                                "other@test.com"
                        )
                );

        assertEquals(
                "You are not authorized to update this application.",
                exception.getMessage()
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void updateStatus_shouldRejectInvalidStatus() {

        User recruiter = mock(User.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getRole()).thenReturn("RECRUITER");

        Job job = new Job();
        job.setRecruiter(recruiter);

        Application application = new Application();
        application.setJob(job);
        application.setStatus("APPLIED");

        when(applicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(userRepository.findByEmail("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService.updateStatus(
                                10L,
                                "INVALID_STATUS",
                                "recruiter@test.com"
                        )
                );

        assertTrue(
                exception.getMessage()
                        .startsWith("Invalid status.")
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void updateStatus_shouldRejectChangingFinalizedApplication() {

        User recruiter = mock(User.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getRole()).thenReturn("RECRUITER");

        Job job = new Job();
        job.setRecruiter(recruiter);

        Application application = new Application();
        application.setJob(job);
        application.setStatus("REJECTED");

        when(applicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(userRepository.findByEmail("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService.updateStatus(
                                10L,
                                "SELECTED",
                                "recruiter@test.com"
                        )
                );

        assertEquals(
                "This application is already finalized and cannot be changed.",
                exception.getMessage()
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void applyForJob_shouldRejectDuplicateApplication() {

        User applicant = mock(User.class);

        when(applicant.getId()).thenReturn(5L);
        when(applicant.getRole()).thenReturn("JOB_SEEKER");

        Job job = new Job();

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(applicant));

        when(jobRepository.findById(20L))
                .thenReturn(Optional.of(job));

        when(applicationRepository
                .existsByApplicantIdAndJobId(5L, 20L))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> applicationService.applyForJob(
                                "seeker@test.com",
                                20L,
                                null
                        )
                );

        assertEquals(
                "You have already applied for this job.",
                exception.getMessage()
        );

        verify(applicationRepository, never())
                .save(any(Application.class));
    }

    @Test
    void getApplicationsByEmail_shouldReturnApplications() {

        Application application = new Application();

        when(applicationRepository
                .findByApplicantEmail("seeker@test.com"))
                .thenReturn(List.of(application));

        List<Application> result =
                applicationService.getApplicationsByEmail(
                        "seeker@test.com"
                );

        assertEquals(1, result.size());
        assertSame(application, result.get(0));

        verify(applicationRepository)
                .findByApplicantEmail("seeker@test.com");
    }
}