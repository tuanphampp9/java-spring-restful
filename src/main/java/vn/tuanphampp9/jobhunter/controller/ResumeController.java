package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Resume;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResCreateResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResFetchResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResUpdateResumeDTO;
import vn.tuanphampp9.jobhunter.service.ResumeService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

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

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
        return ResponseEntity.ok().body(this.resumeService.handleGetAllResume(spec, pageable));
    }

}
