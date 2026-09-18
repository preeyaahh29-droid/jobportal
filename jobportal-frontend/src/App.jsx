import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8080";

// Adds the JWT to every API request when the user is logged in.
// Content-Type is not forced here so FormData uploads continue to work.
const apiFetch = async (url, options = {}) => {
  const token = localStorage.getItem("token");

  const headers = {
    ...(options.headers || {}),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return window.fetch(url, {
    ...options,
    headers,
  });
};

function getStoredUser() {
  try {
    const storedUser = localStorage.getItem("user");
    const token = localStorage.getItem("token");

    if (!storedUser || !token) return null;

    return JSON.parse(storedUser);
  } catch {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    return null;
  }
}

function App() {
  /* ================= AUTH ================= */

  const [isLogin, setIsLogin] = useState(true);
  const [user, setUser] = useState(() => getStoredUser());
  const [isLoggedIn, setIsLoggedIn] = useState(() => getStoredUser() !== null);

  const [loginData, setLoginData] = useState({
    email: "",
    password: "",
  });

  const [registerData, setRegisterData] = useState({
    name: "",
    email: "",
    password: "",
    role: "JOB_SEEKER",
  });

  const [message, setMessage] = useState("");

  /* ================= JOBS ================= */

  const [jobs, setJobs] = useState([]);
  const [loadingJobs, setLoadingJobs] = useState(false);

  const [searchTerm, setSearchTerm] = useState("");
  const [locationFilter, setLocationFilter] = useState("");
  const [minSalary, setMinSalary] = useState("");

  /* ================= RESUME ================= */

  const [resumeFile, setResumeFile] = useState(null);
  const [resumeName, setResumeName] = useState("");

  /* ================= APPLICATIONS ================= */

  const [appliedJobs, setAppliedJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loadingApplications, setLoadingApplications] =
    useState(false);

  /* ================= RECRUITER ================= */

  const [selectedJobId, setSelectedJobId] = useState(null);
  const [selectedJobTitle, setSelectedJobTitle] = useState("");
  const [jobApplicants, setJobApplicants] = useState([]);
  const [loadingApplicants, setLoadingApplicants] =
    useState(false);

  /* ================= JOB FORM ================= */

  const [jobForm, setJobForm] = useState({
    title: "",
    company: "",
    location: "",
    description: "",
    salary: "",
    jobType: "Full Time",
    experience: "0-2 Years",
    skills: "",
  });

  const [editingJobId, setEditingJobId] = useState(null);

  /* ================= JOB DETAILS ================= */

  const [selectedJob, setSelectedJob] = useState(null);
  const [showJobDetails, setShowJobDetails] = useState(false);

  /* ================= PROFILE ================= */

  const [showProfile, setShowProfile] = useState(false);
  const [profile, setProfile] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [editingProfile, setEditingProfile] = useState(false);

  const [profileForm, setProfileForm] = useState({
    phone: "",
    location: "",
    headline: "",
    about: "",
    preferredJobRole: "",
    skills: "",
    education: "",
    experience: "",
    projects: "",
    preferredLocation: "",
    expectedSalary: "",
    jobType: "Full Time",
  });

  /* =========================================================
     LOGIN
     ========================================================= */

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const response = await apiFetch(
        `${API_URL}/api/users/login`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(loginData),
        }
      );

      const text = await response.text();

      let data = {};
      try {
        data = text ? JSON.parse(text) : {};
      } catch {
        data = {};
      }

      if (response.ok && data.token) {
        localStorage.setItem("token", data.token);
        localStorage.setItem("user", JSON.stringify(data));

        setUser(data);
        setIsLoggedIn(true);
        setLoginData({
          email: "",
          password: "",
        });
        setMessage("");
      } else {
        setMessage(
          data.message ||
            data.error ||
            (response.ok
              ? "Login succeeded but no JWT token was returned."
              : "Invalid email or password.")
        );
      }
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to backend. Make sure Spring Boot is running on port 8080."
      );
    }
  };

  /* =========================================================
     REGISTER
     ========================================================= */

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      const response = await apiFetch(
        `${API_URL}/api/users/register`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(registerData),
        }
      );

      const text = await response.text();

      let data = {};
      try {
        data = text ? JSON.parse(text) : {};
      } catch {
        data = {};
      }

      if (response.ok) {
        setMessage(
          "Registration successful! You can now login. ✅"
        );

        setLoginData({
          email: registerData.email,
          password: registerData.password,
        });

        setIsLogin(true);

        setRegisterData({
          name: "",
          email: "",
          password: "",
          role: "JOB_SEEKER",
        });
      } else {
        setMessage(
          data.message ||
            data.error ||
            "Registration failed."
        );
      }
    } catch (error) {
      console.error(error);
      setMessage(
        "Cannot connect to backend."
      );
    }
  };

  /* =========================================================
     JOBS
     ========================================================= */

  const fetchJobs = async () => {
    setLoadingJobs(true);

    try {
      const response = await apiFetch(
        `${API_URL}/api/jobs`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch jobs");
      }

      const data = await response.json();

      setJobs(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error(error);
      setMessage("Unable to load jobs.");
    } finally {
      setLoadingJobs(false);
    }
  };

  const fetchRecruiterJobs = async (recruiterId) => {
    if (!recruiterId) return;

    setLoadingJobs(true);

    try {
      const response = await apiFetch(
        `${API_URL}/api/jobs/recruiter/${recruiterId}`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch recruiter jobs");
      }

      const data = await response.json();

      setJobs(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error(error);
      setMessage("Unable to load your job postings.");
    } finally {
      setLoadingJobs(false);
    }
  };

  const filteredJobs = jobs.filter((job) => {
    const search = searchTerm.toLowerCase().trim();
    const location = locationFilter.toLowerCase().trim();

    const matchesSearch =
      search === "" ||
      job.title?.toLowerCase().includes(search) ||
      job.company?.toLowerCase().includes(search) ||
      job.description?.toLowerCase().includes(search) ||
      job.skills?.toLowerCase().includes(search);

    const matchesLocation =
      location === "" ||
      job.location?.toLowerCase().includes(location);

    const matchesSalary =
      minSalary === "" ||
      Number(job.salary || 0) >= Number(minSalary);

    return (
      matchesSearch &&
      matchesLocation &&
      matchesSalary
    );
  });

  const resetFilters = () => {
    setSearchTerm("");
    setLocationFilter("");
    setMinSalary("");
  };

  /* =========================================================
     JOB MATCHING
     Frontend-only smart matching using the saved job seeker
     profile and each job's requirements.
     ========================================================= */

  const normalizeWords = (value = "") =>
    value
      .toLowerCase()
      .replace(/[^a-z0-9+#.\s-]/g, " ")
      .split(/[\s,|/]+/)
      .map((word) => word.trim())
      .filter(Boolean);

  const getProfileSkills = () => {
    return (profile?.skills || profileForm.skills || "")
      .split(",")
      .map((skill) => skill.trim().toLowerCase())
      .filter(Boolean);
  };

  const getJobSkills = (job) => {
    return (job?.skills || "")
      .split(",")
      .map((skill) => skill.trim().toLowerCase())
      .filter(Boolean);
  };

  const skillMatches = (candidateSkill, requiredSkill) => {
    const candidate = candidateSkill.toLowerCase().trim();
    const required = requiredSkill.toLowerCase().trim();

    if (!candidate || !required) return false;
    if (candidate === required) return true;
    if (candidate.includes(required) || required.includes(candidate)) return true;

    const candidateWords = normalizeWords(candidate);
    const requiredWords = normalizeWords(required);

    return requiredWords.length > 0 &&
      requiredWords.every((word) => candidateWords.includes(word));
  };

  const getJobMatch = (job) => {
    if (!job || user?.role !== "JOB_SEEKER") {
      return {
        score: null,
        matchedSkills: [],
        missingSkills: [],
        label: "Complete your profile",
      };
    }

    const candidateSkills = getProfileSkills();
    const requiredSkills = getJobSkills(job);

    if (candidateSkills.length === 0) {
      return {
        score: 0,
        matchedSkills: [],
        missingSkills: requiredSkills,
        label: "Add skills to improve match",
      };
    }

    const matchedSkills = requiredSkills.filter((required) =>
      candidateSkills.some((candidate) =>
        skillMatches(candidate, required)
      )
    );

    const missingSkills = requiredSkills.filter(
      (required) => !matchedSkills.includes(required)
    );

    const skillScore =
      requiredSkills.length > 0
        ? (matchedSkills.length / requiredSkills.length) * 60
        : 30;

    const preferredRole = (
      profile?.preferredJobRole || profileForm.preferredJobRole || ""
    ).toLowerCase().trim();

    const jobTitle = (job.title || "").toLowerCase();
    const jobDescription = (job.description || "").toLowerCase();

    const roleScore =
      preferredRole &&
      (jobTitle.includes(preferredRole) ||
        preferredRole.includes(jobTitle) ||
        jobDescription.includes(preferredRole))
        ? 15
        : preferredRole
        ? 0
        : 7.5;

    const preferredLocation = (
      profile?.preferredLocation || profileForm.preferredLocation || ""
    ).toLowerCase().trim();

    const jobLocation = (job.location || "").toLowerCase();

    const locationScore =
      preferredLocation &&
      (jobLocation.includes(preferredLocation) ||
        preferredLocation.includes(jobLocation))
        ? 10
        : preferredLocation
        ? 0
        : 5;

    const preferredJobType = (
      profile?.jobType || profileForm.jobType || ""
    ).toLowerCase().trim();

    const jobType = (job.jobType || "").toLowerCase().trim();

    const jobTypeScore =
      preferredJobType && jobType &&
      preferredJobType === jobType
        ? 5
        : preferredJobType && jobType
        ? 0
        : 2.5;

    const expectedSalary = Number(
      profile?.expectedSalary || profileForm.expectedSalary || 0
    );
    const jobSalary = Number(job.salary || 0);

    const salaryScore =
      expectedSalary > 0 && jobSalary >= expectedSalary
        ? 10
        : expectedSalary > 0
        ? 0
        : 5;

    const score = Math.min(100, Math.round(
      skillScore + roleScore + locationScore + jobTypeScore + salaryScore
    ));

    let label = "Low match";
    if (score >= 80) label = "Excellent match";
    else if (score >= 60) label = "Good match";
    else if (score >= 40) label = "Partial match";

    return {
      score,
      matchedSkills,
      missingSkills,
      label,
    };
  };

  const getMatchStyle = (score) => {
    if (score === null) return {};
    if (score >= 80) return { border: "1px solid #22c55e" };
    if (score >= 60) return { border: "1px solid #eab308" };
    return { border: "1px solid #ef4444" };
  };

  /* =========================================================
     JOB DETAILS
     ========================================================= */

  const handleViewJob = (job) => {
    setSelectedJob(job);
    setShowJobDetails(true);

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const closeJobDetails = () => {
    setSelectedJob(null);
    setShowJobDetails(false);
  };

  /* =========================================================
     RESUME
     ========================================================= */

  const handleResumeChange = (e) => {
    const file = e.target.files?.[0];

    if (!file) return;

    const allowedTypes = [
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];

    const allowedExtensions = [
      ".pdf",
      ".doc",
      ".docx",
    ];

    const fileName = file.name.toLowerCase();

    const validType = allowedTypes.includes(file.type);

    const validExtension = allowedExtensions.some(
      (extension) => fileName.endsWith(extension)
    );

    if (!validType && !validExtension) {
      setMessage(
        "Please select a PDF, DOC, or DOCX resume."
      );

      e.target.value = "";
      setResumeFile(null);
      setResumeName("");
      return;
    }

    setResumeFile(file);
    setResumeName(file.name);
    setMessage("Resume selected successfully! 📄");
  };
  
  const handleViewResume = async (resumeUrl) => {
  if (!resumeUrl) {
    setMessage("Resume is not available.");
    return;
  }

  const token = localStorage.getItem("token");

  if (!token) {
    setMessage("Please login again.");
    return;
  }

  const newWindow = window.open("", "_blank");

  try {
    const response = await apiFetch(
      `${API_URL}/api/applications/resume/${encodeURIComponent(resumeUrl)}`
    );

    if (!response.ok) {
      if (newWindow) {
        newWindow.close();
      }

      setMessage(
        `Unable to open resume. HTTP ${response.status}`
      );

      return;
    }

    const blob = await response.blob();

    const blobUrl = window.URL.createObjectURL(blob);

    if (newWindow) {
      newWindow.location.href = blobUrl;
    }

    setTimeout(() => {
      window.URL.revokeObjectURL(blobUrl);
    }, 60000);

  } catch (error) {
    console.error("Resume viewing error:", error);

    if (newWindow) {
      newWindow.close();
    }

    setMessage("Unable to open resume.");
  }
};

  /* =========================================================
     VIEW RESUME
     Uses apiFetch so the JWT Authorization header is included.
     ========================================================= */

  
  /* =========================================================
     APPLICATIONS
     ========================================================= */

  const fetchApplications = async (email) => {
    if (!email) return;

    setLoadingApplications(true);

    try {
      const response = await apiFetch(
        `${API_URL}/api/applications/email/${encodeURIComponent(
          email
        )}`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch applications");
      }

      const data = await response.json();

      const list = Array.isArray(data) ? data : [];

      setApplications(list);

      const jobIds = list
        .filter(
          (application) =>
            application.job &&
            application.job.id !== undefined
        )
        .map(
          (application) => application.job.id
        );

      setAppliedJobs(jobIds);
    } catch (error) {
      console.error(error);
      setMessage(
        "Unable to load your applications."
      );
    } finally {
      setLoadingApplications(false);
    }
  };

  const fetchApplicants = async (
    jobId,
    jobTitle
  ) => {
    setLoadingApplicants(true);

    setSelectedJobId(jobId);
    setSelectedJobTitle(jobTitle);
    setJobApplicants([]);
    setMessage("");

    try {
      const response = await apiFetch(
        `${API_URL}/api/applications/job/${jobId}`
      );

      if (!response.ok) {
        throw new Error("Failed to fetch applicants");
      }

      const data = await response.json();

      setJobApplicants(
        Array.isArray(data) ? data : []
      );
    } catch (error) {
      console.error(error);
      setMessage("Unable to load applicants.");
    } finally {
      setLoadingApplicants(false);
    }
  };

  const closeApplicants = () => {
    setSelectedJobId(null);
    setSelectedJobTitle("");
    setJobApplicants([]);
  };

  const updateApplicationStatus = async (
    applicationId,
    status
  ) => {
    setMessage("");

    try {
      const response = await apiFetch(
        `${API_URL}/api/applications/${applicationId}/status?status=${encodeURIComponent(
          status
        )}`,
        {
          method: "PUT",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update status");
      }

      setMessage(
        `Application ${status.toLowerCase()} successfully! ✅`
      );

      if (selectedJobId) {
        await fetchApplicants(
          selectedJobId,
          selectedJobTitle
        );
      }
    } catch (error) {
      console.error(error);
      setMessage(
        "Unable to update application status."
      );
    }
  };

  /* =========================================================
     APPLY
     ========================================================= */
  const handleApply = async (jobId) => {

  if (!user || !user.id) {
    setMessage("Please login again.");
    return;
  }

  if (!resumeFile) {
    setMessage("Please upload your resume before applying. 📄");
    return;
  }

  if (appliedJobs.includes(jobId)) {
    setMessage("You have already applied for this job.");
    return;
  }

  try {

    const formData = new FormData();

    // IMPORTANT
    formData.append("userId", user.id);

    formData.append("resume", resumeFile);

    const response = await apiFetch(
      `${API_URL}/api/applications/job/${jobId}`,
      {
        method: "POST",
        body: formData,
      }
    );

    const text = await response.text();

    let data = null;

    try {
      data = text ? JSON.parse(text) : null;
    } catch {
      data = null;
    }

    if (response.ok) {

      setAppliedJobs((previous) =>
        previous.includes(jobId)
          ? previous
          : [...previous, jobId]
      );

      setMessage(
        "Application submitted successfully! 🎉"
      );

      await fetchApplications(user.email);

    } else {

      console.error(
        "Application failed:",
        response.status,
        data
      );

      setMessage(
        data?.message ||
        data?.error ||
        `Failed to submit application. HTTP ${response.status}`
      );
    }

  } catch (error) {

    console.error("Apply error:", error);

    setMessage(
      "Cannot connect to backend. Make sure Spring Boot is running."
    );
  }
};
  

  /* =========================================================
     WITHDRAW
     ========================================================= */

  const handleWithdrawApplication = async (
    applicationId
  ) => {
    const confirmed = window.confirm(
      "Are you sure you want to withdraw this application?"
    );

    if (!confirmed) return;

    const applicationToDelete =
      applications.find(
        (application) =>
          application.id === applicationId
      );

    try {
      const response = await apiFetch(
        `${API_URL}/api/applications/${applicationId}`,
        {
          method: "DELETE",
        }
      );

      if (response.ok) {
        setApplications((previous) =>
          previous.filter(
            (application) =>
              application.id !== applicationId
          )
        );

        if (
          applicationToDelete?.job?.id !==
          undefined
        ) {
          setAppliedJobs((previous) =>
            previous.filter(
              (jobId) =>
                jobId !==
                applicationToDelete.job.id
            )
          );
        }

        setMessage(
          "Application withdrawn successfully. ✅"
        );
      } else {
        const text = await response.text();
        setMessage(
          text ||
            "Failed to withdraw application."
        );
      }
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to backend.");
    }
  };

  /* =========================================================
     JOB CRUD
     ========================================================= */

  const handleJobFormChange = (e) => {
    const { name, value } = e.target;

    setJobForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const resetJobForm = () => {
    setJobForm({
      title: "",
      company: "",
      location: "",
      description: "",
      salary: "",
      jobType: "Full Time",
      experience: "0-2 Years",
      skills: "",
    });

    setEditingJobId(null);
  };

  const handleJobSubmit = async (e) => {
    e.preventDefault();

    if (!user || user.role !== "RECRUITER") {
      setMessage(
        "Only recruiters can post or edit jobs."
      );
      return;
    }

    if (
      !jobForm.title.trim() ||
      !jobForm.company.trim() ||
      !jobForm.location.trim() ||
      !jobForm.description.trim() ||
      !jobForm.salary ||
      !jobForm.skills.trim()
    ) {
      setMessage(
        "Please fill in all job details."
      );
      return;
    }

    const jobData = {
      title: jobForm.title.trim(),
      company: jobForm.company.trim(),
      location: jobForm.location.trim(),
      description: jobForm.description.trim(),
      salary: Number(jobForm.salary),
      jobType: jobForm.jobType,
      experience: jobForm.experience,
      skills: jobForm.skills.trim(),
    };

    try {
      let response;

      if (editingJobId !== null) {
        response = await apiFetch(
          `${API_URL}/api/jobs/${editingJobId}?recruiterId=${user.id}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(jobData),
          }
        );
      } else {
        response = await apiFetch(
          `${API_URL}/api/jobs?recruiterId=${user.id}`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(jobData),
          }
        );
      }

      const text = await response.text();

      let data = null;

      try {
        data = text ? JSON.parse(text) : null;
      } catch {
        data = null;
      }

      if (response.ok) {
        setMessage(
          editingJobId !== null
            ? "Job updated successfully! ✅"
            : "Job posted successfully! 🎉"
        );

        resetJobForm();

        await fetchRecruiterJobs(user.id);
      } else {
        setMessage(
          data?.message ||
            data?.error ||
            "Failed to save job."
        );
      }
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to backend.");
    }
  };

  const handleEditJob = (job) => {
    setEditingJobId(job.id);

    setJobForm({
      title: job.title || "",
      company: job.company || "",
      location: job.location || "",
      description: job.description || "",
      salary:
        job.salary !== undefined &&
        job.salary !== null
          ? job.salary
          : "",
      jobType: job.jobType || "Full Time",
      experience:
        job.experience || "0-2 Years",
      skills: job.skills || "",
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  const handleDeleteJob = async (jobId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this job?"
    );

    if (!confirmed) return;

    try {
      const response = await apiFetch(
        `${API_URL}/api/jobs/${jobId}?recruiterId=${user.id}`,
        {
          method: "DELETE",
        }
      );

      if (response.ok) {
        setMessage(
          "Job deleted successfully. ✅"
        );

        if (selectedJobId === jobId) {
          closeApplicants();
        }

        await fetchRecruiterJobs(user.id);
      } else {
        const text = await response.text();
        setMessage(
          text || "Failed to delete job."
        );
      }
    } catch (error) {
      console.error(error);
      setMessage("Cannot connect to backend.");
    }
  };

  /* =========================================================
     APPLICATION TRACKER
     ========================================================= */

  const normalizeApplicationStatus = (status) => {
    return String(status || "APPLIED")
      .trim()
      .toUpperCase()
      .replace(/\s+/g, "_");
  };

  const applicationStages = [
    "APPLIED",
    "VIEWED",
    "SHORTLISTED",
    "INTERVIEW",
    "SELECTED",
  ];

  const getTrackerIndex = (status) => {
    const normalized = normalizeApplicationStatus(status);
    if (normalized === "REJECTED") return -1;
    const index = applicationStages.indexOf(normalized);
    return index === -1 ? 0 : index;
  };

  const getStatusLabel = (status) => {
    const normalized = normalizeApplicationStatus(status);
    const labels = {
      APPLIED: "Applied",
      VIEWED: "Recruiter Viewed",
      SHORTLISTED: "Shortlisted",
      INTERVIEW: "Interview",
      SELECTED: "Selected",
      REJECTED: "Rejected",
    };
    return labels[normalized] || normalized.replace(/_/g, " ");
  };

  const getStatusEmoji = (status) => {
    const normalized = normalizeApplicationStatus(status);
    const emojis = {
      APPLIED: "📨",
      VIEWED: "👀",
      SHORTLISTED: "⭐",
      INTERVIEW: "🎤",
      SELECTED: "🎉",
      REJECTED: "❌",
    };
    return emojis[normalized] || "📌";
  };

  const renderApplicationTracker = (application) => {
  const status = normalizeApplicationStatus(application?.status);

  if (status === "REJECTED") {
    return (
      <div className="application-tracker rejected-tracker">
        <div className="tracker-title">
          Application Status
        </div>

        <div className="rejected-status">
          <span className="status-icon">❌</span>

          <div>
            <strong>Application Rejected</strong>

            <p>
              Your application was not selected for the next
              stage this time.
            </p>
          </div>
        </div>

        <div className="timeline">
          {applicationStages
            .slice(0, 2)
            .map((stage) => (
              <div
                key={stage}
                className="timeline-item completed"
              >
                <div className="timeline-dot">
                  ✓
                </div>

                <div className="timeline-content">
                  <strong>
                    {getStatusEmoji(stage)}{" "}
                    {getStatusLabel(stage)}
                  </strong>

                  <span>Completed</span>
                </div>
              </div>
            ))}

          <div className="timeline-item rejected">
            <div className="timeline-dot">
              ❌
            </div>

            <div className="timeline-content">
              <strong>
                ❌ Rejected
              </strong>

              <span>Application closed</span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const currentIndex = getTrackerIndex(status);

  return (
    <div className="application-tracker">

      <div className="tracker-header">
        <div>
          <h4>Application Status</h4>
          <p>Track your application progress</p>
        </div>

        <div className="current-status-badge">
          {getStatusEmoji(status)}{" "}
          {getStatusLabel(status)}
        </div>
      </div>

      <div className="timeline">

        {applicationStages.map((stage, index) => {
          const completed = index <= currentIndex;
          const current = index === currentIndex;

          return (
            <div
              className={`timeline-item ${
                completed ? "completed" : ""
              } ${current ? "current" : ""}`}
              key={stage}
            >

              <div className="timeline-dot">
                {completed
                  ? "✓"
                  : getStatusEmoji(stage)}
              </div>

              <div className="timeline-content">

                <strong>
                  {getStatusEmoji(stage)}{" "}
                  {getStatusLabel(stage)}
                </strong>

                <span>
                  {current
                    ? "Current stage"
                    : completed
                    ? "Completed"
                    : "Pending"}
                </span>

              </div>

              {index < applicationStages.length - 1 && (
                <div
                  className={`timeline-line ${
                    index < currentIndex
                      ? "completed-line"
                      : ""
                  }`}
                />
              )}

            </div>
          );
        })}

      </div>

      <div className="tracker-footer">
        Current status:
        <strong>
          {" "}
          {getStatusEmoji(status)}{" "}
          {getStatusLabel(status)}
        </strong>
      </div>

    </div>
  );
};

  /* =========================================================
     PROFILE
     ========================================================= */

  const emptyProfileForm = () => ({
    phone: "", location: "", headline: "", about: "",
    preferredJobRole: "", preferredLocation: "", expectedSalary: "",
    jobType: "Full Time", skills: "", education: "",
    experience: "", projects: "",
  });

  const fetchProfile = async (userId) => {
    if (!userId) return;
    setLoadingProfile(true);
    try {
      const response = await apiFetch(`${API_URL}/api/jobseeker/profile/${userId}`);
      if (response.status === 404) {
        setProfile(null);
        setProfileForm(emptyProfileForm());
        return;
      }
      if (!response.ok) throw new Error(`Profile request failed: ${response.status}`);
      const data = await response.json();
      setProfile(data);
      setProfileForm({
        phone: data.phone || "", location: data.location || "",
        headline: data.headline || "", about: data.about || "",
        preferredJobRole: data.preferredJobRole || "",
        preferredLocation: data.preferredLocation || "",
        expectedSalary: data.expectedSalary ?? "",
        jobType: data.jobType || "Full Time",
        skills: data.skills || "", education: data.education || "",
        experience: data.experience || "", projects: data.projects || "",
      });
    } catch (error) {
      console.error("Profile loading error:", error);
      setMessage("Unable to load your professional profile.");
    } finally { setLoadingProfile(false); }
  };

  const handleProfileChange = (e) => {
    const { name, value } = e.target;
    setProfileForm((previous) => ({ ...previous, [name]: value }));
  };

  const calculateProfileCompleteness = () => {
    const fields = [
      profileForm.headline, profileForm.phone, profileForm.location,
      profileForm.about, profileForm.preferredJobRole,
      profileForm.preferredLocation, profileForm.expectedSalary,
      profileForm.jobType, profileForm.skills, profileForm.education,
      profileForm.experience, profileForm.projects,
    ];
    const completed = fields.filter((field) => field !== null && field !== undefined && String(field).trim() !== "").length;
    return Math.round((completed / fields.length) * 100);
  };

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    if (!user?.id) { setMessage("User information is missing. Please login again."); return; }
    setMessage("");
    const profileData = {
      phone: profileForm.phone.trim(),
      location: profileForm.location.trim(),
      headline: profileForm.headline.trim(),
      about: profileForm.about.trim(),
      preferredJobRole: profileForm.preferredJobRole.trim(),
      preferredLocation: profileForm.preferredLocation.trim(),
      expectedSalary: profileForm.expectedSalary !== "" ? Number(profileForm.expectedSalary) : null,
      jobType: profileForm.jobType,
      skills: profileForm.skills.trim(),
      education: profileForm.education.trim(),
      experience: profileForm.experience.trim(),
      projects: profileForm.projects.trim(),
    };
    try {
      const response = await apiFetch(`${API_URL}/api/jobseeker/profile/${user.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(profileData),
      });
      const text = await response.text();
      let data = null;
      try { data = text ? JSON.parse(text) : null; } catch { data = text || null; }
      if (!response.ok) {
        console.error("Profile save failed:", response.status, data);
        setMessage(typeof data === "string" ? data : data?.message || data?.error || `Unable to save profile (HTTP ${response.status}).`);
        return;
      }
      setProfile(data);
      setEditingProfile(false);
      setMessage("Professional profile saved successfully! ✅");
      await fetchProfile(user.id);
    } catch (error) {
      console.error("Profile save error:", error);
      setMessage("Cannot connect to profile backend. Make sure Spring Boot is running on port 8080.");
    }
  };

  const openProfile = async () => {
    setShowProfile(true); setEditingProfile(false); setMessage("");
    if (user?.role === "JOB_SEEKER" && user?.id) await fetchProfile(user.id);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const closeProfile = () => { setShowProfile(false); setEditingProfile(false); setMessage(""); };

  /* =========================================================
     LOGOUT
     ========================================================= */

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    setIsLoggedIn(false);
    setUser(null);

    setJobs([]);
    setApplications([]);
    setAppliedJobs([]);

    closeApplicants();
    closeJobDetails();

    setShowProfile(false);
    setProfile(null);

    setMessage("");

    setResumeFile(null);
    setResumeName("");

    resetFilters();
    resetJobForm();
    setProfileForm(emptyProfileForm());

    setIsLogin(true);
  };

  /* =========================================================
     LOAD DATA AFTER LOGIN
     ========================================================= */

  useEffect(() => {
    if (!isLoggedIn || !user) return;

    if (user.role === "RECRUITER") {
      fetchRecruiterJobs(user.id);
    } else {
      fetchJobs();
      fetchApplications(user.email);
      fetchProfile(user.id);
    }
  }, [isLoggedIn, user]);

  useEffect(() => {
  if (
    !isLoggedIn ||
    !user ||
    user.role !== "JOB_SEEKER"
  ) {
    return;
  }

  const interval = setInterval(() => {
    fetchApplications(user.email);
  }, 10000);

  return () => {
    clearInterval(interval);
  };
}, [isLoggedIn, user]);

  /* =========================================================
     PROFILE PAGE
     ========================================================= */

  if (
    isLoggedIn &&
    user?.role === "JOB_SEEKER" &&
    showProfile
  ) {
    const completeness = calculateProfileCompleteness();

    return (
      <div className="app">
        <div className="dashboard">

          <div className="dashboard-header">
            <div>
              <h1>JobPortal</h1>
              <p>Professional Profile</p>
            </div>

            <div>
              <button
                className="refresh-btn"
                onClick={closeProfile}
                style={{ marginRight: "10px" }}
              >
                ← Back to Jobs
              </button>

              <button
                className="logout-btn"
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          </div>

          {message && (
            <div className="message">
              {message}
            </div>
          )}

          {loadingProfile ? (
            <div className="jobs-section">
              <p className="status-text">
                Loading your professional profile...
              </p>
            </div>
          ) : editingProfile || !profile ? (
            <div className="jobs-section">

              <h2>
                {profile
                  ? "Edit Professional Profile ✏️"
                  : "Create Your Professional Profile 👤"}
              </h2>

              <p>
                Build your profile so recruiters can
                understand your skills and career goals.
              </p>

              <div style={{ marginBottom: "20px", padding: "15px", borderRadius: "10px", background: "#f5f5f5" }}>
                <strong>Profile Completeness: {completeness}%</strong>
                <div style={{ marginTop: "8px", height: "10px", background: "#ddd", borderRadius: "10px", overflow: "hidden" }}>
                  <div style={{ width: `${completeness}%`, height: "100%", background: "#2563eb" }} />
                </div>
              </div>

              <form onSubmit={handleProfileSubmit}>

                <label>Professional Headline</label>

                <input
                  type="text"
                  name="headline"
                  placeholder="Example: Java Developer | AI & Data Science Student"
                  value={profileForm.headline}
                  onChange={handleProfileChange}
                  required
                />

                <label>Phone</label>

                <input
                  type="tel"
                  name="phone"
                  placeholder="Enter your phone number"
                  value={profileForm.phone}
                  onChange={handleProfileChange}
                />

                <label>Current Location</label>

                <input
                  type="text"
                  name="location"
                  placeholder="Example: Chennai"
                  value={profileForm.location}
                  onChange={handleProfileChange}
                />

                <label>About You</label>

                <textarea
                  name="about"
                  rows="6"
                  placeholder="Write about yourself, your skills, interests and career goals..."
                  value={profileForm.about}
                  onChange={handleProfileChange}
                />

                <label>Skills</label>
                <textarea name="skills" rows="3" placeholder="Example: Java, Spring Boot, SQL, React" value={profileForm.skills} onChange={handleProfileChange} />

                <label>Education</label>
                <textarea name="education" rows="3" placeholder="Example: B.Tech AI & Data Science" value={profileForm.education} onChange={handleProfileChange} />

                <label>Experience</label>
                <textarea name="experience" rows="3" placeholder="Example: Fresher / Internship / Work experience" value={profileForm.experience} onChange={handleProfileChange} />

                <label>Projects</label>
                <textarea name="projects" rows="4" placeholder="Mention your important projects" value={profileForm.projects} onChange={handleProfileChange} />

                <label>Preferred Job Role</label>

                <input
                  type="text"
                  name="preferredJobRole"
                  placeholder="Example: Java Developer"
                  value={profileForm.preferredJobRole}
                  onChange={handleProfileChange}
                />

                <label>Preferred Location</label>

                <input
                  type="text"
                  name="preferredLocation"
                  placeholder="Example: Chennai / Bangalore / Remote"
                  value={profileForm.preferredLocation}
                  onChange={handleProfileChange}
                />

                <label>Expected Annual Salary</label>

                <input
                  type="number"
                  name="expectedSalary"
                  placeholder="Example: 600000"
                  min="0"
                  value={profileForm.expectedSalary}
                  onChange={handleProfileChange}
                />

                <label>Preferred Job Type</label>

                <select
                  name="jobType"
                  value={profileForm.jobType}
                  onChange={handleProfileChange}
                >
                  <option value="Full Time">
                    Full Time
                  </option>

                  <option value="Part Time">
                    Part Time
                  </option>

                  <option value="Internship">
                    Internship
                  </option>

                  <option value="Contract">
                    Contract
                  </option>
                </select>

                <button
                  type="submit"
                  className="submit-btn"
                >
                  Save Professional Profile
                </button>

                {profile && (
                  <button
                    type="button"
                    className="refresh-btn"
                    onClick={() =>
                      setEditingProfile(false)
                    }
                    style={{
                      width: "100%",
                      marginTop: "10px",
                    }}
                  >
                    Cancel
                  </button>
                )}
              </form>
            </div>
          ) : (
            <>
              <div className="jobs-section">

                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    gap: "15px",
                    flexWrap: "wrap",
                  }}
                >
                  <div>
                    <h1>
                      {user.name} 👤
                    </h1>

                    <h3>
                      {profile.headline ||
                        "Professional Profile"}
                    </h3>

                    <p>
                      📍{" "}
                      {profile.location ||
                        "Location not added"}
                    </p>
                  </div>

                  <button
                    className="submit-btn"
                    onClick={() =>
                      setEditingProfile(true)
                    }
                  >
                    Edit Profile ✏️
                  </button>
                </div>

              </div>

              <div className="jobs-section">
                <h2>About</h2>

                <p>
                  {profile.about ||
                    "Add an About section to tell recruiters about yourself."}
                </p>
              </div>

              <div className="jobs-section">
                <h2>Skills 🛠️</h2>
                <p>{profile.skills || "No skills added yet."}</p>
              </div>

              <div className="jobs-section">
                <h2>Education 🎓</h2>
                <p>{profile.education || "No education details added yet."}</p>
              </div>

              <div className="jobs-section">
                <h2>Experience 💼</h2>
                <p>{profile.experience || "No experience details added yet."}</p>
              </div>

              <div className="jobs-section">
                <h2>Projects 🚀</h2>
                <p>{profile.projects || "No projects added yet."}</p>
              </div>

              <div className="jobs-section">
                <h2>Career Preferences 🎯</h2>

                <div className="job-meta">
                  <span>
                    💼 Preferred Role:{" "}
                    {profile.preferredJobRole ||
                      "Not specified"}
                  </span>

                  <span>
                    📍 Preferred Location:{" "}
                    {profile.preferredLocation ||
                      "Not specified"}
                  </span>
                </div>

                <div className="salary">
                  💰 Expected Salary:{" "}
                  {profile.expectedSalary
                    ? `₹${Number(
                        profile.expectedSalary
                      ).toLocaleString("en-IN")} / year`
                    : "Not specified"}
                </div>

                <p>
                  🏢 Job Type:{" "}
                  {profile.jobType ||
                    "Not specified"}
                </p>
              </div>

              <div className="jobs-section">
                <h2>Contact Information 📞</h2>

                <p>
                  📧 <strong>Email:</strong>{" "}
                  {user.email}
                </p>

                <p>
                  📱 <strong>Phone:</strong>{" "}
                  {profile.phone ||
                    "Not added"}
                </p>
              </div>
            </>
          )}
        </div>
      </div>
    );
  }

  /* =========================================================
     RECRUITER DASHBOARD
     ========================================================= */

  if (
    isLoggedIn &&
    user?.role === "RECRUITER"
  ) {
    return (
      <div className="app">
        <div className="dashboard">

          <div className="dashboard-header">
            <div>
              <h1>JobPortal</h1>
              <p>Recruiter Dashboard</p>
            </div>

            <button
              className="logout-btn"
              onClick={handleLogout}
            >
              Logout
            </button>
          </div>

          <div className="welcome-section">
            <h2>
              Welcome, {user.name} 👨‍💼
            </h2>

            <p>
              Manage your job postings,
              applicants and hiring process.
            </p>
          </div>

          {message && (
            <div className="message">
              {message}
            </div>
          )}

          <div className="jobs-section">
            <h2>
              {editingJobId !== null
                ? "Edit Job ✏️"
                : "Post a New Job ➕"}
            </h2>

            <form onSubmit={handleJobSubmit}>

              <label>Job Title</label>

              <input
                type="text"
                name="title"
                placeholder="Example: Java Developer"
                value={jobForm.title}
                onChange={handleJobFormChange}
                required
              />

              <label>Company</label>

              <input
                type="text"
                name="company"
                placeholder="Example: TCS"
                value={jobForm.company}
                onChange={handleJobFormChange}
                required
              />

              <label>Location</label>

              <input
                type="text"
                name="location"
                placeholder="Example: Chennai"
                value={jobForm.location}
                onChange={handleJobFormChange}
                required
              />

              <label>Annual Salary</label>

              <input
                type="number"
                name="salary"
                placeholder="Example: 600000"
                value={jobForm.salary}
                onChange={handleJobFormChange}
                min="0"
                required
              />

              <label>Job Type</label>

              <select
                name="jobType"
                value={jobForm.jobType}
                onChange={handleJobFormChange}
              >
                <option>Full Time</option>
                <option>Part Time</option>
                <option>Internship</option>
                <option>Contract</option>
              </select>

              <label>Experience</label>

              <select
                name="experience"
                value={jobForm.experience}
                onChange={handleJobFormChange}
              >
                <option>Fresher</option>
                <option>0-2 Years</option>
                <option>2-5 Years</option>
                <option>5+ Years</option>
              </select>

              <label>Required Skills</label>

              <input
                type="text"
                name="skills"
                placeholder="Example: Java, Spring Boot, MySQL"
                value={jobForm.skills}
                onChange={handleJobFormChange}
                required
              />

              <label>Job Description</label>

              <textarea
                name="description"
                rows="5"
                placeholder="Enter job description..."
                value={jobForm.description}
                onChange={handleJobFormChange}
                required
              />

              <button
                type="submit"
                className="submit-btn"
              >
                {editingJobId !== null
                  ? "Update Job"
                  : "Post Job"}
              </button>

              {editingJobId !== null && (
                <button
                  type="button"
                  className="refresh-btn"
                  onClick={resetJobForm}
                  style={{
                    width: "100%",
                    marginTop: "10px",
                  }}
                >
                  Cancel Edit
                </button>
              )}
            </form>
          </div>

          <div className="jobs-section">

            <div className="section-header">
              <h2>Your Job Postings 💼</h2>

              <button
                onClick={() =>
                  fetchRecruiterJobs(user.id)
                }
                className="refresh-btn"
              >
                Refresh Jobs
              </button>
            </div>

            {loadingJobs ? (
              <p className="status-text">
                Loading jobs...
              </p>
            ) : jobs.length === 0 ? (
              <p className="status-text">
                No jobs posted yet.
              </p>
            ) : (
              <div className="jobs-grid">

                {jobs.map((job) => (
                  <div
                    className="job-card"
                    key={job.id}
                  >

                    <div className="job-card-top">
                      <div>
                        <h3>{job.title}</h3>

                        <p className="company-name">
                          🏢 {job.company}
                        </p>
                      </div>

                      <span className="job-type-badge">
                        {job.jobType ||
                          "Full Time"}
                      </span>
                    </div>

                    <div className="job-meta">
                      <span>
                        📍 {job.location}
                      </span>

                      <span>
                        💼{" "}
                        {job.experience ||
                          "Fresher"}
                      </span>
                    </div>

                    <div className="salary">
                      💰 ₹
                      {Number(
                        job.salary || 0
                      ).toLocaleString("en-IN")}
                      {" / year"}
                    </div>

                    {user?.role === "JOB_SEEKER" && (
                      <div
                        style={{
                          margin: "10px 0",
                          padding: "10px 12px",
                          borderRadius: "10px",
                          background: "#f8fafc",
                          ...getMatchStyle(getJobMatch(job).score),
                        }}
                      >
                        <strong>🎯 {getJobMatch(job).score}% Match</strong>
                        <div style={{ fontSize: "13px", marginTop: "4px" }}>
                          {getJobMatch(job).label}
                        </div>
                      </div>
                    )}

                    {job.skills && (
                      <div className="skills">
                        {job.skills
                          .split(",")
                          .map(
                            (skill, index) => (
                              <span key={index}>
                                {skill.trim()}
                              </span>
                            )
                          )}
                      </div>
                    )}

                    <p className="job-description">
                      {job.description}
                    </p>

                    <button
                      className="submit-btn"
                      onClick={() =>
                        handleEditJob(job)
                      }
                    >
                      Edit ✏️
                    </button>

                    <button
                      className="logout-btn"
                      onClick={() =>
                        handleDeleteJob(job.id)
                      }
                      style={{
                        marginLeft: "8px",
                      }}
                    >
                      Delete 🗑️
                    </button>

                    <button
                      className="apply-btn"
                      onClick={() =>
                        fetchApplicants(
                          job.id,
                          job.title
                        )
                      }
                      style={{
                        display: "block",
                        marginTop: "12px",
                        width: "100%",
                      }}
                    >
                      View Applicants 👥
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {selectedJobId !== null && (
            <div className="applications-section">

              <div className="section-header">
                <h2>
                  Applicants for{" "}
                  {selectedJobTitle} 👥
                </h2>

                <button
                  className="refresh-btn"
                  onClick={() =>
                    fetchApplicants(
                      selectedJobId,
                      selectedJobTitle
                    )
                  }
                >
                  Refresh Applicants
                </button>
              </div>

              <button
                className="logout-btn"
                onClick={closeApplicants}
              >
                Close Applicants
              </button>

              {loadingApplicants ? (
                <p className="status-text">
                  Loading applicants...
                </p>
              ) : jobApplicants.length === 0 ? (
                <p className="status-text">
                  No applicants for this job yet.
                </p>
              ) : (
                <div className="applications-grid">

                  {jobApplicants.map(
                    (application) => (
                      <div
                        className="application-card"
                        key={application.id}
                      >

                        <h3>
                          {
                            application.applicantName
                          }
                        </h3>

                        <p>
                          <strong>
                            Email:
                          </strong>{" "}
                          {
                            application.applicantEmail
                          }
                        </p>

                        <p>
                          <strong>
                            Job:
                          </strong>{" "}
                          {application.job?.title ||
                            selectedJobTitle}
                        </p>

                        {application.resumeUrl && (
                          <button
                            type="button"
                            className="submit-btn"
                            onClick={() =>
                              handleViewResume(
                                application.resumeUrl
                              )
                            }
                          >
                            View Resume 📄
                          </button>
                        )}

                        <p>
                          <strong>
                            Status:
                          </strong>{" "}
                          <span className="application-status">
                            {application.status ||
                              "APPLIED"}
                          </span>
                        </p>

                        <div className="application-actions">

                          <button
                            className="status-btn applied-btn"
                            onClick={() =>
                              updateApplicationStatus(
                                application.id,
                                "APPLIED"
                              )
                            }
                          >
                            Applied
                          </button>

                          <button
                            className="status-btn shortlist-btn"
                            onClick={() =>
                              updateApplicationStatus(
                                application.id,
                                "SHORTLISTED"
                              )
                            }
                          >
                            Shortlist
                          </button>

                          <button
                            className="status-btn reject-btn"
                            onClick={() =>
                              updateApplicationStatus(
                                application.id,
                                "REJECTED"
                              )
                            }
                          >
                            Reject
                          </button>

                          <select
                            value={application.status || "APPLIED"}
                            onChange={(e) =>
                              updateApplicationStatus(
                                application.id,
                                e.target.value
                              )
                            }
                            style={{
                              marginLeft: "8px",
                              padding: "7px 10px",
                              borderRadius: "8px",
                              border: "1px solid #ccc"
                            }}
                          >
                            <option value="APPLIED">Applied</option>
                            <option value="VIEWED">Viewed</option>
                            <option value="SHORTLISTED">Shortlisted</option>
                            <option value="INTERVIEW">Interview</option>
                            <option value="SELECTED">Selected</option>
                            <option value="REJECTED">Rejected</option>
                          </select>

                        </div>
                      </div>
                    )
                  )}

                </div>
              )}
            </div>
          )}

        </div>
      </div>
    );
  }

  /* =========================================================
     JOB SEEKER DASHBOARD
     ========================================================= */

  if (
    isLoggedIn &&
    user?.role === "JOB_SEEKER"
  ) {
    return (
      <div className="app">
        <div className="dashboard">

          <div className="dashboard-header">
            <div>
              <h1>JobPortal</h1>

              <p>
                Find your next opportunity
              </p>
            </div>

            <div>

              <button
                className="submit-btn"
                onClick={openProfile}
                style={{
                  marginRight: "10px",
                }}
              >
                My Profile 👤
              </button>

              <button
                className="logout-btn"
                onClick={handleLogout}
              >
                Logout
              </button>

            </div>
          </div>

          <div className="welcome-section">

            <h2>
              Welcome, {user.name} 👋
            </h2>

            <p>
              Discover jobs that match your
              skills and career goals.
            </p>

            <button
              className="refresh-btn"
              onClick={openProfile}
              style={{
                marginTop: "15px",
              }}
            >
              Complete Your Professional Profile →
            </button>

          </div>

          {message && (
            <div className="message">
              {message}
            </div>
          )}

          {/* RESUME */}

          <div className="jobs-section resume-section">

            <h2>Your Resume 📄</h2>

            <p>
              Upload your latest resume before
              applying for jobs.
            </p>

            <input
              type="file"
              accept=".pdf,.doc,.docx"
              onChange={handleResumeChange}
            />

            {resumeName && (
              <p className="resume-selected">
                Selected Resume:{" "}
                <strong>{resumeName}</strong>
              </p>
            )}

          </div>

          {/* JOBS */}

          <div className="jobs-section">

            <div className="section-header">

              <h2>Find Jobs 💼</h2>

              <button
                onClick={fetchJobs}
                className="refresh-btn"
              >
                Refresh Jobs
              </button>

            </div>

            <div className="job-filters">

              <input
                type="text"
                placeholder="🔎 Search job, company or skill..."
                value={searchTerm}
                onChange={(e) =>
                  setSearchTerm(e.target.value)
                }
              />

              <input
                type="text"
                placeholder="📍 Location"
                value={locationFilter}
                onChange={(e) =>
                  setLocationFilter(e.target.value)
                }
              />

              <input
                type="number"
                placeholder="💰 Minimum salary"
                value={minSalary}
                onChange={(e) =>
                  setMinSalary(e.target.value)
                }
              />

              <button
                type="button"
                className="refresh-btn"
                onClick={resetFilters}
              >
                Reset
              </button>

            </div>

            {!loadingJobs &&
              jobs.length > 0 && (
                <p className="filter-result">
                  Showing{" "}
                  <strong>
                    {filteredJobs.length}
                  </strong>{" "}
                  of{" "}
                  <strong>{jobs.length}</strong>{" "}
                  available jobs
                </p>
              )}

            {loadingJobs ? (
              <p className="status-text">
                Finding available jobs...
              </p>
            ) : filteredJobs.length === 0 ? (
              <p className="status-text">
                No jobs match your current
                search or filters.
              </p>
            ) : (
              <div className="jobs-grid">

                {filteredJobs.map((job) => (
                  <div
                    className="job-card"
                    key={job.id}
                  >

                    <div className="job-card-top">

                      <div>
                        <h3>{job.title}</h3>

                        <p className="company-name">
                          🏢 {job.company}
                        </p>
                      </div>

                      <span className="job-type-badge">
                        {job.jobType ||
                          "Full Time"}
                      </span>

                    </div>

                    <div className="job-meta">

                      <span>
                        📍 {job.location}
                      </span>

                      <span>
                        💼{" "}
                        {job.experience ||
                          "Fresher"}
                      </span>

                    </div>

                    <div className="salary">
                      💰 ₹
                      {Number(
                        job.salary || 0
                      ).toLocaleString("en-IN")}
                      {" / year"}
                    </div>

                    {job.skills && (
                      <div className="skills">
                        {job.skills
                          .split(",")
                          .map(
                            (skill, index) => (
                              <span key={index}>
                                {skill.trim()}
                              </span>
                            )
                          )}
                      </div>
                    )}

                    <p className="job-description">
                      {job.description}
                    </p>

                    {job.postedDate && (
                      <p className="posted-date">
                        🕐 Posted{" "}
                        {new Date(
                          job.postedDate
                        ).toLocaleDateString(
                          "en-IN"
                        )}
                      </p>
                    )}

                    <div
                      className="job-card-actions"
                      style={{
                        display: "flex",
                        gap: "10px",
                        flexWrap: "wrap",
                      }}
                    >

                      <button
                        className="refresh-btn"
                        onClick={() =>
                          handleViewJob(job)
                        }
                      >
                        View Details 👁️
                      </button>

                      <button
                        className="apply-btn"
                        onClick={() =>
                          handleApply(job.id)
                        }
                        disabled={appliedJobs.includes(
                          job.id
                        )}
                      >
                        {appliedJobs.includes(
                          job.id
                        )
                          ? "Applied ✅"
                          : "Apply Now"}
                      </button>

                    </div>

                  </div>
                ))}

              </div>
            )}

          </div>

          {/* JOB DETAILS */}

          {showJobDetails &&
            selectedJob && (
              <div className="jobs-section">

                <div className="section-header">

                  <h2>
                    {selectedJob.title}
                  </h2>

                  <button
                    className="logout-btn"
                    onClick={closeJobDetails}
                  >
                    Close
                  </button>

                </div>

                <h3>
                  🏢 {selectedJob.company}
                </h3>

                <p>
                  📍 {selectedJob.location}
                </p>

                <p>
                  💼{" "}
                  {selectedJob.jobType ||
                    "Full Time"}
                </p>

                <p>
                  💰 ₹
                  {Number(
                    selectedJob.salary || 0
                  ).toLocaleString("en-IN")}
                  {" / year"}
                </p>

                <p>
                  👨‍💼{" "}
                  {selectedJob.experience ||
                    "Fresher"}
                </p>

                {user?.role === "JOB_SEEKER" && (
                  <div
                    style={{
                      margin: "20px 0",
                      padding: "16px",
                      borderRadius: "12px",
                      background: "#f8fafc",
                      ...getMatchStyle(getJobMatch(selectedJob).score),
                    }}
                  >
                    <h3 style={{ marginTop: 0 }}>🎯 Your Job Match</h3>
                    <div style={{ fontSize: "24px", fontWeight: "700" }}>
                      {getJobMatch(selectedJob).score}%
                    </div>
                    <p style={{ marginBottom: "10px" }}>
                      {getJobMatch(selectedJob).label}
                    </p>

                    {getJobMatch(selectedJob).matchedSkills.length > 0 && (
                      <>
                        <strong>✅ Skills you match</strong>
                        <div className="skills" style={{ marginTop: "8px" }}>
                          {getJobMatch(selectedJob).matchedSkills.map((skill, index) => (
                            <span key={index}>{skill}</span>
                          ))}
                        </div>
                      </>
                    )}

                    {getJobMatch(selectedJob).missingSkills.length > 0 && (
                      <>
                        <strong style={{ display: "block", marginTop: "12px" }}>
                          ⚠️ Skills to improve
                        </strong>
                        <div className="skills" style={{ marginTop: "8px" }}>
                          {getJobMatch(selectedJob).missingSkills.map((skill, index) => (
                            <span key={index}>{skill}</span>
                          ))}
                        </div>
                      </>
                    )}
                  </div>
                )}

                {selectedJob.skills && (
                  <>
                    <h3>Required Skills</h3>

                    <div className="skills">
                      {selectedJob.skills
                        .split(",")
                        .map(
                          (skill, index) => (
                            <span key={index}>
                              {skill.trim()}
                            </span>
                          )
                        )}
                    </div>
                  </>
                )}

                <h3>
                  Job Description
                </h3>

                <p>
                  {selectedJob.description}
                </p>

                <button
                  className="apply-btn"
                  onClick={() =>
                    handleApply(
                      selectedJob.id
                    )
                  }
                  disabled={appliedJobs.includes(
                    selectedJob.id
                  )}
                >
                  {appliedJobs.includes(
                    selectedJob.id
                  )
                    ? "Already Applied ✅"
                    : "Apply Now"}
                </button>

              </div>
            )}

          {/* APPLICATIONS */}

          <div className="applications-section">

            <div className="section-header">

              <h2>
                My Applications 📋
              </h2>

              <button
                onClick={() =>
                  fetchApplications(
                    user.email
                  )
                }
                className="refresh-btn"
              >
                Refresh Applications
              </button>

            </div>

            {loadingApplications ? (
              <p className="status-text">
                Loading your applications...
              </p>
            ) : applications.length === 0 ? (
              <p className="status-text">
                You haven't applied for any
                jobs yet.
              </p>
            ) : (
              <div className="applications-grid">

                {applications.map(
                  (application) => (
                    <div
                      className="application-card"
                      key={application.id}
                    >

                      <h3>
                        {application.job?.title ||
                          "Job Application"}
                      </h3>

                      {application.job && (
                        <>
                          <p>
                            <strong>
                              Company:
                            </strong>{" "}
                            {
                              application.job
                                .company
                            }
                          </p>

                          <p>
                            <strong>
                              Location:
                            </strong>{" "}
                            {
                              application.job
                                .location
                            }
                          </p>

                          <p>
                            <strong>
                              Salary:
                            </strong>{" "}
                            ₹
                            {Number(
                              application.job
                                .salary || 0
                            ).toLocaleString(
                              "en-IN"
                            )}
                          </p>
                        </>
                      )}

                      <p>
                        <strong>
                          Status:
                        </strong>{" "}

                        <span className="application-status">
                          {application.status ||
                            "APPLIED"}
                        </span>
                      </p>

                      {renderApplicationTracker(application)}

                      {application.resumeUrl && (
                        <p>
                          <strong>
                            Resume:
                          </strong>{" "}

                          <button
                            type="button"
                            className="submit-btn"
                            onClick={() =>
                              handleViewResume(
                                application.resumeUrl
                              )
                            }
                          >
                            View Resume 📄
                          </button>
                        </p>
                      )}

                      <button
                        className="withdraw-btn"
                        onClick={() =>
                          handleWithdrawApplication(
                            application.id
                          )
                        }
                      >
                        Withdraw Application
                      </button>

                    </div>
                  )
                )}

              </div>
            )}

          </div>

        </div>
      </div>
    );
  }

  /* =========================================================
     LOGIN / REGISTER
     ========================================================= */

  return (
    <div className="app">

      <div className="container">

        <div className="logo">

          <h1>JobPortal</h1>

          <p>
            Find opportunities. Build your career.
          </p>

        </div>

        <div className="tabs">

          <button
            className={isLogin ? "active" : ""}
            onClick={() => {
              setIsLogin(true);
              setMessage("");
            }}
          >
            Login
          </button>

          <button
            className={!isLogin ? "active" : ""}
            onClick={() => {
              setIsLogin(false);
              setMessage("");
            }}
          >
            Register
          </button>

        </div>

        {message && (
          <div className="message">
            {message}
          </div>
        )}

        {isLogin ? (
          <form onSubmit={handleLogin}>

            <h2>Welcome Back 👋</h2>

            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={loginData.email}
              onChange={(e) =>
                setLoginData({
                  ...loginData,
                  email: e.target.value,
                })
              }
              required
            />

            <label>Password</label>

            <input
              type="password"
              placeholder="Enter your password"
              value={loginData.password}
              onChange={(e) =>
                setLoginData({
                  ...loginData,
                  password: e.target.value,
                })
              }
              required
            />

            <button
              type="submit"
              className="submit-btn"
            >
              Login
            </button>

            <p className="switch-text">
              Don't have an account?

              <span
                onClick={() => {
                  setIsLogin(false);
                  setMessage("");
                }}
              >
                Register
              </span>
            </p>

          </form>
        ) : (
          <form onSubmit={handleRegister}>

            <h2>
              Create Your Account 📝
            </h2>

            <label>Full Name</label>

            <input
              type="text"
              placeholder="Enter your full name"
              value={registerData.name}
              onChange={(e) =>
                setRegisterData({
                  ...registerData,
                  name: e.target.value,
                })
              }
              required
            />

            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={registerData.email}
              onChange={(e) =>
                setRegisterData({
                  ...registerData,
                  email: e.target.value,
                })
              }
              required
            />

            <label>Password</label>

            <input
              type="password"
              placeholder="Create a password"
              value={registerData.password}
              onChange={(e) =>
                setRegisterData({
                  ...registerData,
                  password: e.target.value,
                })
              }
              minLength="4"
              required
            />

            <label>Account Type</label>

            <select
              value={registerData.role}
              onChange={(e) =>
                setRegisterData({
                  ...registerData,
                  role: e.target.value,
                })
              }
            >
              <option value="JOB_SEEKER">
                Job Seeker
              </option>

              <option value="RECRUITER">
                Recruiter
              </option>
            </select>

            <button
              type="submit"
              className="submit-btn"
            >
              Create Account
            </button>

            <p className="switch-text">
              Already have an account?

              <span
                onClick={() => {
                  setIsLogin(true);
                  setMessage("");
                }}
              >
                Login
              </span>
            </p>

          </form>
        )}

      </div>
    </div>
  );
}

export default App;