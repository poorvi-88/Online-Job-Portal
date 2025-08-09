package in.sp.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDTO {
    private Long applicationId;
    private Long jobSeekerId;
    private String name;
    private String email;
    private String status;
    private String resumeUrl;
}
