package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.entity.User;
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
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void getAllJobs_shouldReturnJobs() {

        Job job = new Job();

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        List<Job> result = jobService.getAllJobs();

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }

    @Test
    void getJobById_shouldReturnJob() {

        Job job = new Job();

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        Job result = jobService.getJobById(1L);

        assertSame(job, result);
    }

    @Test
    void getJobsByRecruiter_shouldReturnJobs() {

        Job job = new Job();

        when(jobRepository.findByRecruiterId(5L))
                .thenReturn(List.of(job));

        List<Job> result =
                jobService.getJobsByRecruiter(5L);

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }

    @Test
    void createJob_shouldCreateJobForRecruiter() {

        User recruiter = mock(User.class);

        when(recruiter.getRole())
                .thenReturn("RECRUITER");

        Job job = new Job();

        when(userRepository.findByEmail("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.save(job))
                .thenReturn(job);

        Job result =
                jobService.createJob(
                        "recruiter@test.com",
                        job
                );

        assertSame(recruiter, job.getRecruiter());
        assertSame(job, result);

        verify(jobRepository).save(job);
    }

    @Test
    void createJob_shouldRejectJobSeeker() {

        User user = mock(User.class);

        when(user.getRole())
                .thenReturn("JOB_SEEKER");

        Job job = new Job();

        when(userRepository.findByEmail("seeker@test.com"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> jobService.createJob(
                                "seeker@test.com",
                                job
                        )
                );

        assertEquals(
                "Only recruiters can post jobs.",
                exception.getMessage()
        );

        verify(jobRepository, never())
                .save(any(Job.class));
    }

    @Test
    void searchByTitle_shouldReturnMatchingJobs() {

        Job job = new Job();

        when(jobRepository
                .findByTitleContainingIgnoreCase("Java"))
                .thenReturn(List.of(job));

        List<Job> result =
                jobService.searchByTitle("Java");

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }

    @Test
    void searchByLocation_shouldReturnMatchingJobs() {

        Job job = new Job();

        when(jobRepository
                .findByLocationContainingIgnoreCase("Chennai"))
                .thenReturn(List.of(job));

        List<Job> result =
                jobService.searchByLocation("Chennai");

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }

    @Test
    void searchByCompany_shouldReturnMatchingJobs() {

        Job job = new Job();

        when(jobRepository
                .findByCompanyContainingIgnoreCase("TCS"))
                .thenReturn(List.of(job));

        List<Job> result =
                jobService.searchByCompany("TCS");

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }

    @Test
    void filterBySalary_shouldReturnMatchingJobs() {

        Job job = new Job();

        when(jobRepository
                .findBySalaryGreaterThanEqual(500000.0))
                .thenReturn(List.of(job));

        List<Job> result =
                jobService.filterBySalary(500000.0);

        assertEquals(1, result.size());
        assertSame(job, result.get(0));
    }
}