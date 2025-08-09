package in.sp.main.controller;

import in.sp.main.entity.Job;
import in.sp.main.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService service;

    @PostMapping("/post")
    public Job postJob(@RequestBody Job job) {
        return service.postJob(job);
    }

    @GetMapping("/all")
    public List<Job> getAllJobs() {
        return service.getAllJobs();
    }

    @GetMapping("/recruiter/{recruiterId}")
    public List<Job> getJobsByRecruiter(@PathVariable Long recruiterId) {
        return service.getJobsByRecruiter(recruiterId);
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return service.getJobById(id);
    }
}
