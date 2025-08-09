package in.sp.main.dto;

import in.sp.main.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class JobWithApplicationDTO {
    private Long jobId;
    private String title;
    private String company;
    private String description;
    private LocalDate appliedDate;
    private JobApplication.ApplicationStatus status;

}
