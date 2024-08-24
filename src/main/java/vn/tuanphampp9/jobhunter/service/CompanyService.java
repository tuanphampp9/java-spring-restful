package vn.tuanphampp9.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.repository.CompanyRepository;
import vn.tuanphampp9.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec,
            Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// current start from 1
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageCompany.getTotalElements());// amount of elements
        meta.setPages(pageCompany.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageCompany.getContent());
        return resultPaginationDTO;
    }

    public Company handleGetCompanyById(Long id) {
        return this.companyRepository.findById(id).orElse(null);
    }

    public void handleDeleteCompany(Long id) {
        // delete all users of this company
        List<User> users = this.userRepository.findByCompany(this.companyRepository.findById(id).get());
        this.userRepository.deleteAll(users);
        this.companyRepository.deleteById(id);
    }
}
