# JobPortal – Online Job Portal with Employer Dashboard

## 1. Title

JobPortal – Online Job Portal with Employer Dashboard

## 2. Domain

Recruitment and Employment

## 3. User Types

- Job Seeker
- Employer / Recruiter

## 4. Problem Statement

Job seekers need a centralized platform to search for suitable job opportunities, maintain their professional profile, upload their resume, submit applications, and track the status of their applications. Employers need a structured platform to create and manage job postings, view applicants, and process applications efficiently. Managing these activities without a unified workflow can make the recruitment process difficult to organize and track. JobPortal provides a web-based platform that connects job seekers and employers through a structured recruitment and application workflow.

## 5. Proposed Solution

JobPortal is a web-based recruitment platform that connects job seekers with employers. Job seekers can register and log in, maintain their profile, upload a resume, search for jobs, and apply for suitable opportunities. Employers can register and log in, create and manage job postings, view applicants, and process applications by updating their status. The system stores users, job seeker profiles, employer profiles, jobs, and applications in a centralized database.

## 6. Core Entities

The system contains five main entities:

1. Users
2. Job Seeker Profiles
3. Employer Profiles
4. Jobs
5. Applications

## 7. User Roles

### Job Seeker

- Register and log in
- Create and manage a job seeker profile
- Upload and manage resume information
- Search and view job opportunities
- Apply for jobs
- Track application status
- Manage submitted applications

### Employer / Recruiter

- Register and log in
- Create and manage employer profile
- Post job opportunities
- Manage job postings
- View applicants for posted jobs
- Process applications
- Update application status

## 8. Success Criteria

The project will be considered successful when:

- Users can register and log in securely.
- Job seekers can create and manage their profiles.
- Employers can create and manage job postings.
- Job seekers can search and apply for jobs.
- Employers can view applicants for their jobs.
- Application status can be tracked and updated.
- User, job, profile, and application data are stored correctly in the database.
- The complete recruitment workflow operates successfully from frontend to backend and database.

## 9. Out of Scope

The initial version does not include:

- Online payment processing
- Payroll management
- Video interview functionality
- Automated background verification
- Full-scale third-party recruitment platform integration

## 10. Chosen Track

Java Track – Spring Boot

### Technology Stack

- Frontend: React.js
- Backend: Java Spring Boot
- Database: PostgreSQL
- Authentication: Spring Security and JWT
- Build Tool: Maven
- API: REST