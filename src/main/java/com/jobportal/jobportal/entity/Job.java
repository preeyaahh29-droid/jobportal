package com.jobportal.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(max = 150, message = "Job title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Company is required")
    @Size(max = 150, message = "Company must not exceed 150 characters")
    private String company;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location must not exceed 150 characters")
    @Column(length = 150)
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 3000, message = "Description must not exceed 3000 characters")
    @Column(length = 3000)
    private String description;

    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotBlank(message = "Skills are required")
    @Size(max = 1000, message = "Skills must not exceed 1000 characters")
    @Column(length = 1000)
    private String skills;

    private LocalDateTime postedDate;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    @JsonIgnoreProperties({
            "password",
            "hibernateLazyInitializer",
            "handler"
    })
    private User recruiter;

    public Job() {
    }

    @PrePersist
    protected void onCreate() {
        if (postedDate == null) {
            postedDate = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public User getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(User recruiter) {
        this.recruiter = recruiter;
    }
}