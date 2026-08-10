package service;
import com.jobportal.jobportal.entity.Job;
import com.jobportal.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class JobService {
    private final JobRepository jobRepository;
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }
    public Job updateJob(Long id, Job jobDetails) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return null;
        }
        job.setTitle(jobDetails.getTitle());
        job.setCompany(jobDetails.getCompany());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setDescription(jobDetails.getDescription());
        return jobRepository.save(job);
    }
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
    public List<Job> searchJobsByTitle(String title) {
    return jobRepository.findByTitleContainingIgnoreCase(title);
    }
}