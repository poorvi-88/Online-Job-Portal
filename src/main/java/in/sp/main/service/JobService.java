package in.sp.main.service;

import in.sp.main.entity.Job;
import java.util.List;

public interface JobService {
    Job postJob(Job job);
    List<Job> getAllJobs();
    List<Job> getJobsByRecruiter(Long recruiterId);
    Job getJobById(Long id);
}
