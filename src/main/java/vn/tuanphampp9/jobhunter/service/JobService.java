package vn.tuanphampp9.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Job;
import vn.tuanphampp9.jobhunter.domain.Skill;
import vn.tuanphampp9.jobhunter.domain.Response.ResCreateJobDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResUpdateDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResUpdateJobDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.repository.CompanyRepository;
import vn.tuanphampp9.jobhunter.repository.JobRepository;
import vn.tuanphampp9.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        // check skills
        if (job.getSkills() != null) {
            List<Long> listSkillIds = job.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(listSkillIds);
            job.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(job.getCompany().getId());
            if (company.isPresent()) {
                job.setCompany(company.get());
            }
        }

        // create job
        Job newJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(newJob.getId());
        resCreateJobDTO.setName(newJob.getName());
        resCreateJobDTO.setSalary(newJob.getSalary());
        resCreateJobDTO.setQuantity(newJob.getQuantity());
        resCreateJobDTO.setLocation(newJob.getLocation());
        resCreateJobDTO.setLevel(newJob.getLevel());
        resCreateJobDTO.setStartDate(newJob.getStartDate());
        resCreateJobDTO.setEndDate(newJob.getEndDate());
        resCreateJobDTO.setActive(newJob.isActive());
        resCreateJobDTO.setCreatedAt(newJob.getCreatedAt());
        resCreateJobDTO.setCreatedBy(newJob.getCreatedBy());

        if (newJob.getSkills() != null) {
            List<String> skills = newJob.getSkills().stream().map(skill -> skill.getName())
                    .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skills);
        }
        return resCreateJobDTO;

    }

    public Optional<Job> handleFindJobById(long id) {
        Job job = this.jobRepository.findById(id);
        return Optional.ofNullable(job);
    }

    public ResUpdateJobDTO handleUpdateJob(Job job, Job oldJob) {
        // check skills
        if (job.getSkills() != null) {
            List<Long> listSkillIds = job.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(listSkillIds);
            oldJob.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(job.getCompany().getId());
            if (company.isPresent()) {
                oldJob.setCompany(company.get());
            }
        }
        oldJob.setName(job.getName());
        oldJob.setSalary(job.getSalary());
        oldJob.setQuantity(job.getQuantity());
        oldJob.setLocation(job.getLocation());
        oldJob.setLevel(job.getLevel());
        oldJob.setStartDate(job.getStartDate());
        oldJob.setEndDate(job.getEndDate());
        oldJob.setActive(job.isActive());

        // update job
        Job updatedJob = this.jobRepository.save(oldJob);

        // convert response
        ResUpdateJobDTO resUpdateDTO = new ResUpdateJobDTO();
        resUpdateDTO.setId(updatedJob.getId());
        resUpdateDTO.setName(updatedJob.getName());
        resUpdateDTO.setSalary(updatedJob.getSalary());
        resUpdateDTO.setQuantity(updatedJob.getQuantity());
        resUpdateDTO.setLocation(updatedJob.getLocation());
        resUpdateDTO.setLevel(updatedJob.getLevel());
        resUpdateDTO.setStartDate(updatedJob.getStartDate());
        resUpdateDTO.setEndDate(updatedJob.getEndDate());
        resUpdateDTO.setActive(updatedJob.isActive());
        resUpdateDTO.setUpdatedAt(updatedJob.getUpdatedAt());
        resUpdateDTO.setUpdatedBy(updatedJob.getUpdatedBy());
        resUpdateDTO.setCreatedAt(updatedJob.getCreatedAt());
        resUpdateDTO.setCreatedBy(updatedJob.getCreatedBy());

        if (updatedJob.getSkills() != null) {
            List<String> skills = updatedJob.getSkills().stream().map(skill -> skill.getName())
                    .collect(Collectors.toList());
            resUpdateDTO.setSkills(skills);
        }
        return resUpdateDTO;
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageJob.getTotalElements());// amount of elements
        meta.setPages(pageJob.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageJob.getContent());
        return resultPaginationDTO;
    }

    public boolean handleExistById(long id) {
        return this.jobRepository.existsById(id);
    }
}
