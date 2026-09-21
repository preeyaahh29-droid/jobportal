\# API Contract — Job Portal



Base URL:



```text

/api

Authentication



Authentication uses JWT through Spring Security.



Roles:



JOB\_SEEKER

RECRUITER



Authenticated requests use:



Authorization: Bearer <JWT>

Health

Method	Endpoint	Access	Purpose

GET	/api/health	Public	Check backend status

Users

Method	Endpoint	Access	Purpose

POST	/api/users/register	Public	Register a new user

POST	/api/users/login	Public	Authenticate user and issue JWT

GET	/api/users	Authenticated	Get users

GET	/api/users/email/{email}	Authenticated	Get user by email

Jobs

Method	Endpoint	Access	Purpose

GET	/api/jobs	Public	Get all jobs

GET	/api/jobs/{id}	Public	Get job by ID

GET	/api/jobs/recruiter/{recruiterId}	Public	Get jobs posted by a recruiter

POST	/api/jobs	Recruiter	Create a job

PUT	/api/jobs/{id}	Recruiter	Update an owned job

DELETE	/api/jobs/{id}	Recruiter	Delete an owned job

GET	/api/jobs/search/title?title=...	Public	Search jobs by title

GET	/api/jobs/search/location?location=...	Public	Search jobs by location

GET	/api/jobs/search/company?company=...	Public	Search jobs by company

GET	/api/jobs/filter/salary?minSalary=...	Public	Filter jobs by minimum salary

Applications

Method	Endpoint	Access	Purpose

POST	/api/applications/job/{jobId}	Job Seeker	Apply for a job with resume

GET	/api/applications	Recruiter	Get applications

GET	/api/applications/email/{email}	Job Seeker	Get applications by applicant email

GET	/api/applications/job/{jobId}	Recruiter	Get applicants for a job

GET	/api/applications/resume/{fileName}	Authenticated	View uploaded resume

PUT	/api/applications/{id}/status?status=...	Recruiter	Update application status

DELETE	/api/applications/{id}	Job Seeker	Withdraw an application



The application endpoint uses:



Content-Type: multipart/form-data



with the uploaded resume as a multipart file.



Job Seeker Profile

Method	Endpoint	Access	Purpose

GET	/api/jobseeker/profile/{userId}	Job Seeker	Get job seeker profile

PUT	/api/jobseeker/profile/{userId}	Job Seeker	Create/update job seeker profile

Employer Profile

Method	Endpoint	Access	Purpose

GET	/api/employer/profile/{userId}	Recruiter	Get employer profile

PUT	/api/employer/profile/{userId}	Recruiter	Create/update employer profile

Resume

Method	Endpoint	Access	Purpose

POST	/api/resumes/upload	Authenticated	Upload resume

GET	/api/resumes/view/{fileName}	Authenticated	View resume

API Documentation



Swagger UI:



https://jobportal-6ib1.onrender.com/swagger-ui/index.html



OpenAPI specification:



https://jobportal-6ib1.onrender.com/v3/api-docs

