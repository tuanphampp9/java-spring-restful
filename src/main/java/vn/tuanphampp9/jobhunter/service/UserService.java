package vn.tuanphampp9.jobhunter.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import vn.tuanphampp9.jobhunter.domain.User;
import vn.tuanphampp9.jobhunter.repository.UserRepository;

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
        if(userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }

    public List<User> handleGetAllUser() {
        return this.userRepository.findAll();
    }
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
