package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Job;
import vn.tuanphampp9.jobhunter.domain.Response.ResCreateJobDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResUpdateJobDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.JobService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create new job")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody Job job) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @PutMapping("jobs")
    @ApiMessage("Update job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job)
            throws IdInvalidException {
        Optional<Job> oldJob = this.jobService.handleFindJobById(job.getId());
        if (oldJob.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job));
    }

    @DeleteMapping("jobs/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable long id)
            throws IdInvalidException {
        Optional<Job> oldJob = this.jobService.handleFindJobById(id);
        if (oldJob.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.handleGetAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJob(@PathVariable long id)
            throws IdInvalidException {
        Optional<Job> job = this.jobService.handleFindJobById(id);
        if (job.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(job.get());
    }

}
