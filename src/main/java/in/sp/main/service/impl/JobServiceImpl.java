package in.sp.main.service.impl;

import in.sp.main.entity.Job;
import in.sp.main.repository.JobRepository;
import in.sp.main.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository repo;

    @Override
    public Job postJob(Job job) {
        return repo.save(job);
    }

    @Override
    public List<Job> getAllJobs() {
        return repo.findAll();
    }

    @Override
    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }

    @Override
    public Job getJobById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
