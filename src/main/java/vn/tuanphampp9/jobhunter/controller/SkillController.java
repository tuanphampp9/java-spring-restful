package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Skill;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.SkillService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create new skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill)
            throws IdInvalidException {
        boolean isSkillExist = this.skillService.existsByName(skill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill name is already exist");
        }
        Skill newSkill = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill)
            throws IdInvalidException {
        Optional<Skill> oldSkill = this.skillService.handleFindSkillById(skill.getId());
        if (oldSkill.isEmpty()) {
            throw new IdInvalidException("Skill not found");
        }
        boolean isSkillExist = this.skillService.existsByName(skill.getName());
        if (isSkillExist) {
            throw new IdInvalidException("Skill name is already exist");
        }
        oldSkill.get().setName(skill.getName());
        Skill updatedSkill = this.skillService.handleCreateSkill(oldSkill.get());

        return ResponseEntity.ok().body(updatedSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.handleGetAllSkills(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id)
            throws IdInvalidException {
        Optional<Skill> skill = this.skillService.handleFindSkillById(id);
        if (skill.isEmpty()) {
            throw new IdInvalidException("Skill not found");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
