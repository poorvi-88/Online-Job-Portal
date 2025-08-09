package in.sp.main.service;

import in.sp.main.dto.ApplicantDTO;
import in.sp.main.dto.JobWithApplicationDTO;
import in.sp.main.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {
    JobApplication applyToJob(Long jobId, Long jobSeekerId);
    List<JobApplication> getApplicationsByJobSeeker(Long jobSeekerId);
    List<JobApplication> getApplicantsByJobId(Long jobId);
    List<ApplicantDTO> getApplicantDetailsByJobId(Long jobId);
    List<JobWithApplicationDTO> getApplicationsWithDetailsByJobSeeker(Long jobSeekerId);
    void updateApplicationStatus(Long applicationId, JobApplication.ApplicationStatus status);

}
