package in.sp.main.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private Long jobSeekerId;

    private LocalDate appliedDate;

    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public enum ApplicationStatus {
        PENDING,
        REJECTED,
        ACCEPTED
    }
}
