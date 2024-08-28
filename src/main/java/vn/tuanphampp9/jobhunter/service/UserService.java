package vn.tuanphampp9.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import vn.tuanphampp9.jobhunter.domain.Company;
import vn.tuanphampp9.jobhunter.domain.Role;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.Response.ResCreateUserDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResUpdateDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResUserDTO;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.repository.UserRepository;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        // check company exist
        if (user.getCompany() != null) {
            Company company = this.companyService.handleGetCompanyById(user.getCompany().getId());
            user.setCompany(company);
        }

        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.handleGetRoleById(user.getRole().getId());
            user.setRole(role);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUser(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.company company = new ResCreateUserDTO.company();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(company);
        }
        return resCreateUserDTO;
    }

    public User handleUpdateUser(User newUser, User userFound) {
        userFound.setAddress(newUser.getAddress());
        userFound.setAge(newUser.getAge());
        userFound.setEmail(newUser.getEmail());
        userFound.setGender(newUser.getGender());
        userFound.setName(newUser.getName());
        // check company exist
        if (newUser.getCompany() != null) {
            Company company = this.companyService.handleGetCompanyById(newUser.getCompany().getId());
            userFound.setCompany(company);
        }
        // check role
        if (newUser.getRole() != null) {
            Role role = this.roleService.handleGetRoleById(newUser.getRole().getId());
            userFound.setRole(role);
        }
        return this.userRepository.save(userFound);
    }

    public User handleUpdateRefreshToken(User user) {
        return this.userRepository.save(user);
    }

    public ResultPaginationDTO handleGetAllUser(
            Specification<User> spec,
            Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setTotal(pageUser.getTotalElements());// amount of elements
        meta.setPages(pageUser.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent().stream().map(
                u -> this.convertToResUserDTO(u))
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listUser);
        return resultPaginationDTO;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean handleCheckUserExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.company company = new ResUserDTO.company();
        ResUserDTO.role role = new ResUserDTO.role();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public ResUpdateDTO convertToResUpdateDTO(User user) {
        ResUpdateDTO res = new ResUpdateDTO();
        ResUpdateDTO.company company = new ResUpdateDTO.company();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.handleUpdateRefreshToken(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public boolean handleExistById(long id) {
        return this.userRepository.existsById(id);
    }

    public boolean handleExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
