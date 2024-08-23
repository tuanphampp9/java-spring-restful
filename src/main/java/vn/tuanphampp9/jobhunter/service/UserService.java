package vn.tuanphampp9.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.domain.DTO.Meta;
import vn.tuanphampp9.jobhunter.domain.DTO.ResCreateUserDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResUpdateDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResUserDTO;
import vn.tuanphampp9.jobhunter.domain.DTO.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.repository.UserRepository;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
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
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }

    public ResultPaginationDTO handleGetAllUser(
            Specification<User> spec,
            Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setTotal(pageUser.getTotalElements());// amount of elements
        meta.setPages(pageUser.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent().stream().map(
                u -> new ResUserDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getName(),
                        u.getGender(),
                        u.getAddress(),
                        u.getAge(),
                        u.getCreatedAt(),
                        u.getUpdatedAt()))
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
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }

    public ResUpdateDTO convertToResUpdateDTO(User user) {
        ResUpdateDTO res = new ResUpdateDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.handleUpdateUser(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
