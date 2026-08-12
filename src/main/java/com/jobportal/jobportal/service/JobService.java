package com.jobportal.jobportal.service;

import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get job by ID
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    // Create job
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // Update job
    public Job updateJob(Long id, Job jobDetails) {

        Job job = jobRepository.findById(id).orElse(null);

        if (job == null) {
            return null;
        }

        job.setTitle(jobDetails.getTitle());
        job.setCompany(jobDetails.getCompany());
        job.setLocation(jobDetails.getLocation());
        job.setDescription(jobDetails.getDescription());
        job.setSalary(jobDetails.getSalary());

        return jobRepository.save(job);
    }

    // Delete job
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    // Search by title
    public List<Job> searchByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    // Search by location
    public List<Job> searchByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    // Search by company
    public List<Job> searchByCompany(String company) {
        return jobRepository.findByCompanyContainingIgnoreCase(company);
    }

    // Filter by minimum salary
    public List<Job> filterBySalary(Float minSalary) {
        return jobRepository.findBySalaryGreaterThanEqual(minSalary);
    }
}