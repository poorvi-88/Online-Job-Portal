package in.sp.main.service.impl;

import in.sp.main.dto.ApplicantDTO;
import in.sp.main.dto.JobWithApplicationDTO;
import in.sp.main.entity.Job;
import in.sp.main.entity.JobApplication;
import in.sp.main.entity.User;
import in.sp.main.repository.JobApplicationRepository;
import in.sp.main.repository.JobRepository;
import in.sp.main.repository.UserRepository;
import in.sp.main.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private JobApplicationRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JobRepository jobRepo;

    @Override
    public JobApplication applyToJob(Long jobId, Long jobSeekerId) {
        Optional<JobApplication> existing = repo.findByJobIdAndJobSeekerId(jobId, jobSeekerId);
        if (existing.isPresent()) {
            throw new RuntimeException("Already applied to this job.");
        }

        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setJobSeekerId(jobSeekerId);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(JobApplication.ApplicationStatus.PENDING);
        return repo.save(application);
    }

    @Override
    public List<JobApplication> getApplicationsByJobSeeker(Long jobSeekerId) {
        return repo.findByJobSeekerId(jobSeekerId);
    }

    @Override
    public void updateApplicationStatus(Long applicationId, JobApplication.ApplicationStatus status) {
        Optional<JobApplication> opt = repo.findById(applicationId);
        if (opt.isPresent()) {
            JobApplication application = opt.get();
            application.setStatus(status);
            repo.save(application);
        } else {
            throw new RuntimeException("Application not found with ID: " + applicationId);
        }
    }

    @Override
    public List<JobApplication> getApplicantsByJobId(Long jobId) {
        return repo.findByJobId(jobId);
    }

    @Override
    public List<ApplicantDTO> getApplicantDetailsByJobId(Long jobId) {
        List<JobApplication> applications = repo.findByJobId(jobId);
        List<ApplicantDTO> dtoList = new ArrayList<>();

        for (JobApplication app : applications) {
            Optional<User> jobSeekerOpt = userRepo.findById(app.getJobSeekerId());
            if (jobSeekerOpt.isPresent()) {
                User jobSeeker = jobSeekerOpt.get();

                ApplicantDTO dto = new ApplicantDTO();
                dto.setApplicationId(app.getId());
                dto.setJobSeekerId(jobSeeker.getId());
                dto.setName(jobSeeker.getName());
                dto.setEmail(jobSeeker.getEmail());
                dto.setResumeUrl(app.getResumeUrl());


                // Safe handling of status
                if (app.getStatus() != null) {
                    dto.setStatus(app.getStatus().toString());
                } else {
                    dto.setStatus("PENDING");
                }

                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    @Override
    public List<JobWithApplicationDTO> getApplicationsWithDetailsByJobSeeker(Long jobSeekerId) {
        List<JobApplication> applications = repo.findByJobSeekerId(jobSeekerId);

        return applications.stream()
                .map(app -> {
                    Optional<Job> jobOpt = jobRepo.findById(app.getJobId());
                    return jobOpt.map(job -> new JobWithApplicationDTO(
                            job.getId(),
                            job.getTitle(),
                            job.getLocation(),
                            job.getDescription(),
                            app.getAppliedDate(),
                            app.getStatus()
                    )).orElse(null);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
