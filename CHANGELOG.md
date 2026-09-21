# Changelog

All notable changes to the JobPortal project are documented here.

## [Review-II / Full Product, Live] - 2026-09-21

### Added
- Added Swagger/OpenAPI documentation.
- Added GitHub Actions CI/CD with test-gated Render deployment.
- Added Docker configuration for cloud deployment.
- Added Render PostgreSQL cloud database deployment.
- Added live Spring Boot backend deployment.
- Added live React frontend deployment.
- Added service-layer tests for JobService and UserService.
- Added JaCoCo service-layer coverage reporting.
- Added API contract documentation.

### Changed
- Updated backend CORS configuration for the deployed frontend URL.
- Updated README with live deployment, API documentation, setup, testing, and deployment information.
- Configured frontend API URL through the Vite environment variable.

## [Review-I / MVP] - 2026-09-18

### Added
- Finalized the JobPortal problem statement.
- Added system architecture, ER, and class diagrams.
- Implemented user registration and login.
- Added JWT-based authentication with Spring Security.
- Added Job Seeker profile management.
- Added job search and salary filtering.
- Added job application workflow.
- Added application status tracking.
- Added health endpoint.
- Added environment variable example file.
- Added MIT License.

### Changed
- Migrated database configuration from MySQL to PostgreSQL.
- Corrected Java package structure.
- Updated README with project documentation and setup instructions.
- Improved backend project structure with configuration, controllers, services, repositories, entities, and security packages.

### Security
- Removed local `application.properties` containing database credentials and JWT secret from Git tracking.
- Added `.gitignore` rules for environment files, local configuration, and uploaded files.