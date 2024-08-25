package vn.tuanphampp9.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Resume;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.Response.ResCreateJobDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResCreateResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResFetchResumeDTO;
import vn.tuanphampp9.jobhunter.domain.Response.resume.ResUpdateResumeDTO;
import vn.tuanphampp9.jobhunter.repository.ResumeRepository;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    public boolean checkExistUserAndJob(Resume resume) {
        if (resume.getUser() == null || resume.getJob() == null) {
            return false;
        }
        if (!this.jobService.handleExistById(resume.getJob().getId())) {
            return false;
        }
        if (!this.userService.handleExistById(resume.getUser().getId())) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO resCreateResumeDTO = new ResCreateResumeDTO();
        resCreateResumeDTO.setId(resume.getId());
        resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());
        resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
        return resCreateResumeDTO;
    }

    public boolean checkExistById(Long id) {
        return this.resumeRepository.existsById(id);
    }

    public Resume handleGetResumeById(Long id) {
        return this.resumeRepository.findById(id).orElse(null);
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO resUpdateResumeDTO = new ResUpdateResumeDTO();
        resUpdateResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resUpdateResumeDTO.setUpdatedBy(resume.getUpdatedBy());
        return resUpdateResumeDTO;
    }

    public void handleDeleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO resFetchResumeDTO = new ResFetchResumeDTO();
        resFetchResumeDTO.setId(resume.getId());
        resFetchResumeDTO.setEmail(resume.getEmail());
        resFetchResumeDTO.setUrl(resume.getUrl());
        resFetchResumeDTO.setCreatedAt(resume.getCreatedAt());
        resFetchResumeDTO.setCreatedBy(resume.getCreatedBy());
        resFetchResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resFetchResumeDTO.setUpdatedBy(resume.getUpdatedBy());
        resFetchResumeDTO.setStatus(resume.getStatus());

        // set info user
        resFetchResumeDTO
                .setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));

        // set info job
        resFetchResumeDTO.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return resFetchResumeDTO;
    }

    public ResultPaginationDTO handleGetAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageResume.getTotalElements());// amount of elements
        meta.setPages(pageResume.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageResume.getContent().stream().map(
                resume -> this.getResume(resume)).collect(Collectors.toList());
        resultPaginationDTO.setResult(listResume);
        return resultPaginationDTO;
    }
}
