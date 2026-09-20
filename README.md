# Job Portal with Employer Dashboard

A full-stack job portal application built with **Spring Boot, React, PostgreSQL, and JWT authentication**.

The system supports separate workflows for **Job Seekers** and **Recruiters**, including job management, applications, resume uploads, application-status tracking, and employer profiles.

## Live Application

**Frontend:**
https://jobportal-frontend-4iox.onrender.com

**Backend API:**
https://jobportal-6ib1.onrender.com

**Swagger UI:**
https://jobportal-6ib1.onrender.com/swagger-ui/index.html

**OpenAPI JSON:**
https://jobportal-6ib1.onrender.com/v3/api-docs

## Features

### Job Seeker

- Register and log in using JWT authentication
- View available jobs
- Search and filter jobs
- View job details
- Apply for jobs
- Upload resumes in PDF, DOC, and DOCX formats
- Prevent duplicate applications
- View submitted applications
- Withdraw applications
- Access uploaded resumes securely
- Manage job-seeker profile

### Recruiter

- Register and log in using JWT authentication
- Create jobs
- Update jobs
- Delete jobs
- View recruiter-owned jobs
- View applicants for owned jobs
- Update application status
- Access applicant resumes securely
- Create and update employer profile

### Application Status

Supported application stages:

```text
APPLIED
VIEWED
SHORTLISTED
INTERVIEW
SELECTED
REJECTED

### Security

JWT-based authentication
Spring Security authorization
Role-based access control
JOB_SEEKER and RECRUITER roles
BCrypt password hashing
Protected resume access
Recruiter ownership checks
CORS configuration for the deployed frontend
Database credentials and JWT secrets kept outside source control

## Technology Stack

### Backend

Java 21
Spring Boot 4.1.0
Spring Web
Spring Data JPA
Spring Security
PostgreSQL
JWT
Bean Validation
Maven

### Frontend

React
Vite
JavaScript
HTML
CSS

### Development & Deployment

Git
GitHub
GitHub Actions
Docker
Render
PostgreSQL

## Project Structure
jobportal/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── docs/
│   ├── application-workflow.png
│   └── Problem_Statement.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jobportal/jobportal/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       ├── security/
│   │   │       └── controllers/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/jobportal/jobportal/
│               ├── JobportalApplicationTests.java
│               └── service/
│                   └── ApplicationServiceTest.java
│
├── jobportal-frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   └── ...
│   ├── package.json
│   └── vite.config.js
│
├── .dockerignore
├── .env.example
├── .gitignore
├── CHANGELOG.md
├── Dockerfile
├── LICENSE
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md

## Database Relationships

The application uses PostgreSQL with the following core relationships:

User
 ├── 1:N → Job
 └── 1:N → Application

Job
 └── 1:N → Application

Application
 ├── N:1 → User
 └── N:1 → Job

Additional profile entities are used for:

User → JobSeekerProfile
User → EmployerProfile

## Local Setup

### Prerequisites

Install:

Java 21
PostgreSQL
Node.js and npm
Git

### Clone the repository

git clone https://github.com/preeyaahh29-droid/jobportal.git
cd jobportal

Backend Setup

Create the PostgreSQL database:

Database name: jobportal

Create the local ignored configuration file:

src/main/resources/application.properties

Example structure:

spring.datasource.url=jdbc:postgresql://localhost:5432/jobportal
spring.datasource.username=postgres
spring.datasource.password=YOUR_LOCAL_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080

jwt.secret=YOUR_LOCAL_JWT_SECRET

Do not commit real passwords or JWT secrets.

Start the backend:

.\mvnw.cmd spring-boot:run

Backend:

http://localhost:8080
Frontend Setup

Open a second terminal:

cd jobportal-frontend
npm.cmd install
npm.cmd run dev

Frontend:

http://localhost:5173

The frontend uses:

VITE_API_URL

for the deployed API URL.

For local development, the application falls back to:

http://localhost:8080

## API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI specification:

http://localhost:8080/v3/api-docs

The deployed API documentation is available at:

https://jobportal-6ib1.onrender.com/swagger-ui/index.html

API Security

Public endpoints include:

GET  /api/health
POST /api/users/register
POST /api/users/login
GET  /api/jobs
GET  /api/jobs/**

Protected operations use JWT authentication and role-based authorization.

Examples:

JOB_SEEKER
POST /api/applications/job/**
GET  /api/applications/email/**
DELETE /api/applications/**

RECRUITER
POST /api/jobs
PUT  /api/jobs
DELETE /api/jobs
GET  /api/applications
GET  /api/applications/job/**
PUT  /api/applications/*/status

Resume access requires authentication and ownership/authorization checks.

## Testing

The project includes automated tests using:

JUnit 5
Mockito
Spring Boot Test

Run the complete test suite:

.\mvnw.cmd clean test

Current test result:

Tests run: 8
Failures: 0
Errors: 0
Skipped: 0

The test suite includes application-service tests covering:

successful application-status updates
recruiter authorization
recruiter ownership validation
invalid status handling
finalized application protection
duplicate application prevention
application retrieval

## CI/CD

GitHub Actions automatically runs the backend test suite on:

pushes to main
pull requests targeting main

Workflow:

.github/workflows/ci.yml

The CI environment uses a temporary PostgreSQL service for automated testing.

## Docker

The backend includes a Docker configuration:

Dockerfile

Build the backend image:

docker build -t jobportal-backend .

The container runs the Spring Boot application using the platform-provided PORT environment variable.

## Cloud Deployment

The application is deployed using Render.

### Deployment Architecture

React Frontend
      │
      ▼
Render Static Site
      │
      │ HTTPS API requests
      ▼
Spring Boot Backend
      │
      ▼
Render PostgreSQL

### Current Deployment

Frontend
https://jobportal-frontend-4iox.onrender.com

Backend
https://jobportal-6ib1.onrender.com

Database
Render PostgreSQL

Production database credentials and JWT secrets are supplied through deployment environment variables and are not stored in Git.

## Documentation

Project problem statement:

Problem Statement

Application workflow:

## GitHub Repository

https://github.com/preeyaahh29-droid/jobportal

## Important Project Files

File	Purpose
pom.xml	Backend dependencies and Maven configuration
Dockerfile	Backend container configuration
.dockerignore	Docker build exclusions
.github/workflows/ci.yml	GitHub Actions CI workflow
jobportal-frontend/	React/Vite frontend
src/main/java/	Spring Boot backend
src/test/java/	Automated tests
docs/	Project documentation and diagrams
CHANGELOG.md	Project change history
LICENSE	MIT License

## Development Notes

Secrets and local credentials are intentionally excluded from Git using .gitignore.

The following configuration should never contain real credentials in source control:

src/main/resources/application.properties
.env

Use environment variables for deployed environments.

## License

This project is licensed under the MIT License.

See LICENSE.
