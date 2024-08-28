package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Job;
import vn.tuanphampp9.jobhunter.domain.Resume;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResCreateResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResFetchResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResUpdateResumeDTO;
import vn.tuanphampp9.jobhunter.service.ResumeService;
import vn.tuanphampp9.jobhunter.service.UserService;
import vn.tuanphampp9.jobhunter.util.SecurityUtil;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    @Autowired
    private FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        // check id user and job exist
        boolean isExist = this.resumeService.checkExistUserAndJob(resume);
        if (!isExist) {
            throw new IdInvalidException("User or Job not exist");
        }
        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume)
            throws IdInvalidException {
        // check id user and job exist
        boolean isExist = this.resumeService.checkExistById(resume.getId());
        if (!isExist) {
            throw new IdInvalidException("Resume is not exist");
        }
        Resume oldResume = this.resumeService.handleGetResumeById(resume.getId());
        oldResume.setStatus(resume.getStatus());
        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.update(oldResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)
            throws IdInvalidException {
        // check id resume exist
        boolean isExist = this.resumeService.checkExistById(id);
        if (!isExist) {
            throw new IdInvalidException("Resume is not exist");
        }
        this.resumeService.handleDeleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResFetchResumeDTO> get(@PathVariable("id") long id)
            throws IdInvalidException {
        // check id resume exist
        boolean isExist = this.resumeService.checkExistById(id);
        if (!isExist) {
            throw new IdInvalidException("Resume is not exist");
        }
        Resume resume = this.resumeService.handleGetResumeById(id);
        return ResponseEntity.ok().body(this.resumeService.getResume(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User userFound = this.userService.handleGetUserByEmail(email);
        if (userFound != null) {
            Company userCompany = userFound.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(Job::getId).toList();
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok().body(this.resumeService.handleGetAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get resume by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
