package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.RestResponse;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.DTO.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.CompanyService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Get all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.companyService.handleGetAllCompanies(spec, pageable));
    }

    @PutMapping("companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company oldCompany = this.companyService.handleGetCompanyById(company.getId());
        if (oldCompany == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        oldCompany.setName(company.getName());
        oldCompany.setDescription(company.getDescription());
        oldCompany.setAddress(company.getAddress());
        oldCompany.setLogo(company.getLogo());
        return ResponseEntity.ok().body(this.companyService.handleSaveCompany(oldCompany));
    }

    @DeleteMapping("companies/{id}")
    public ResponseEntity<RestResponse<Object>> deleteCompany(@PathVariable("id") long id) {
        RestResponse<Object> res = new RestResponse<Object>();
        Company company = this.companyService.handleGetCompanyById(id);
        if (company == null) {
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            res.setError("Company not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
