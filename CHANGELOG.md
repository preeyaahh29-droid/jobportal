# Changelog

All notable changes to the JobPortal project are documented here.

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