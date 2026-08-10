package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return jobService.createJob(job);
    }

    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job job) {
        return jobService.updateJob(id, job);
    }

    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "Job deleted successfully";
    }
    @GetMapping("/search/title")
    public List<Job> searchByTitle(@RequestParam String title) {
    return jobService.searchByTitle(title);
    }

    @GetMapping("/search/location")
    public List<Job> searchByLocation(@RequestParam String location) {
    return jobService.searchByLocation(location);
    }

    @GetMapping("/search/company")
    public List<Job> searchByCompany(@RequestParam String company) {
    return jobService.searchByCompany(company);
    }

    @GetMapping("/filter/salary")
    public List<Job> filterBySalary(@RequestParam Float minSalary) {
    return jobService.filterBySalary(minSalary);
    }
   
}