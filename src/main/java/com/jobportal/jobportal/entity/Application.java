package com.jobportal.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resumeUrl;

    private String status;

    // Many applications can belong to one applicant
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({
            "password",
            "hibernateLazyInitializer",
            "handler"
    })
    private User applicant;

    // Many applications can belong to one job
    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Job job;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
