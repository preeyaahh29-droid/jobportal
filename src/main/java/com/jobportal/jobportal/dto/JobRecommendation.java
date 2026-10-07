package com.jobportal.jobportal.dto;

import java.util.List;

public class JobRecommendation {

    private Long jobId;
    private String title;
    private String company;
    private String location;
    private Double salary;
    private String jobType;
    private String skills;
    private int matchScore;
    private String matchLabel;
    private List<String> matchedSkills;
    private List<String> missingSkills;

    public JobRecommendation() {
    }

    public JobRecommendation(
            Long jobId,
            String title,
            String company,
            String location,
            Double salary,
            String jobType,
            String skills,
            int matchScore,
            String matchLabel,
            List<String> matchedSkills,
            List<String> missingSkills) {

        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.skills = skills;
        this.matchScore = matchScore;
        this.matchLabel = matchLabel;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public Double getSalary() {
        return salary;
    }

    public String getJobType() {
        return jobType;
    }

    public String getSkills() {
        return skills;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public String getMatchLabel() {
        return matchLabel;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }
}