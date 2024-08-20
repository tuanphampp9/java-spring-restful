package vn.tuanphampp9.jobhunter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import vn.tuanphampp9.jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User save(User user);
    void deleteById(Long id);
    List<User> findAll();
    Optional<User> findById(Long id);
    User findByEmail(String email);
}
