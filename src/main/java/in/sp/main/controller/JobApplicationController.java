package in.sp.main.controller;

import in.sp.main.dto.ApplicantDTO;
import in.sp.main.dto.JobWithApplicationDTO;
import in.sp.main.entity.JobApplication;
import in.sp.main.repository.JobApplicationRepository;
import in.sp.main.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobseeker")
public class JobApplicationController {

    @Autowired
    private JobApplicationService service;

    @Autowired
    private JobApplicationRepository repo;

    // 1. Apply to a job
    @PostMapping("/apply")
    public ResponseEntity<String> applyToJob(
            @RequestParam Long jobId,
            @RequestParam Long jobSeekerId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setJobSeekerId(jobSeekerId);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(JobApplication.ApplicationStatus.PENDING);

        // Save resume file if provided
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/resumes/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath);

            // Save the relative URL for later viewing
            application.setResumeUrl("/api/jobseeker/resume/" + fileName);
        }

        repo.save(application);
        return ResponseEntity.ok("Application submitted");
    }

    // 2. Serve resume to be viewable in browser
    @GetMapping("/resume/{filename:.+}")
    public ResponseEntity<Resource> viewResume(@PathVariable String filename) throws MalformedURLException {
        Path filePath = Paths.get("uploads/resumes/").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // 3. Get job seeker’s applications
    @GetMapping("/applied-jobs/{jobSeekerId}")
    public List<JobApplication> getApplications(@PathVariable Long jobSeekerId) {
        return service.getApplicationsByJobSeeker(jobSeekerId);
    }

    // 4. Get applicants for a specific job
    @GetMapping("/job/{jobId}/applicants")
    public List<ApplicantDTO> getApplicantsByJobId(@PathVariable Long jobId) {
        return service.getApplicantDetailsByJobId(jobId);
    }

    // 5. Get application + job details for a job seeker
    @GetMapping("/applied-jobs-with-details/{jobSeekerId}")
    public List<JobWithApplicationDTO> getApplicationsWithDetails(@PathVariable Long jobSeekerId) {
        return service.getApplicationsWithDetailsByJobSeeker(jobSeekerId);
    }

    // 6. Update application status
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") JobApplication.ApplicationStatus status
    ) {
        service.updateApplicationStatus(id, status);
        return ResponseEntity.ok("Status updated to: " + status);
    }

    // 7. Upload resume separately (if not uploaded during apply)
    @PostMapping("/upload-resume/{applicationId}")
    public ResponseEntity<String> uploadResume(
            @PathVariable Long applicationId,
            @RequestParam("file") MultipartFile file) {

        try {
            String uploadDir = "uploads/resumes/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Optional<JobApplication> optionalApp = repo.findById(applicationId);
            if (optionalApp.isPresent()) {
                JobApplication app = optionalApp.get();
                app.setResumeUrl("/api/jobseeker/resume/" + fileName);
                repo.save(app);
                return ResponseEntity.ok("Resume uploaded successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    // 8. Global exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal error: " + ex.getMessage());
    }
}
