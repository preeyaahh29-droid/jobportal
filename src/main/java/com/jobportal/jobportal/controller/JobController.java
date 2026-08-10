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
}