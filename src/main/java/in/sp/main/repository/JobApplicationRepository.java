package in.sp.main.repository;

import in.sp.main.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobSeekerId(Long jobSeekerId);
    List<JobApplication> findByJobId(Long jobId);
    Optional<JobApplication> findByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

}
