package vn.tuanphampp9.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Skill;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean existsByName(String skillName) {
        return this.skillRepository.existsByName(skillName);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Optional<Skill> handleFindSkillById(long id) {
        Skill skill = this.skillRepository.findById(id);
        return Optional.ofNullable(skill);
    }

    public ResultPaginationDTO handleGetAllSkills(Specification<Skill> spec,
            Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageSkill.getTotalElements());// amount of elements
        meta.setPages(pageSkill.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageSkill.getContent());
        return resultPaginationDTO;
    }

    public void handleDeleteSkill(long id) {
        Optional<Skill> skill = this.handleFindSkillById(id);
        Skill skillToDelete = skill.get();
        // delete current skill from all jobs
        skillToDelete.getJobs().forEach(job -> job.getSkills().remove(skillToDelete));
        // delete current skill from all subscribers
        skillToDelete.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(skillToDelete));
        this.skillRepository.deleteById(id);
    }
}
