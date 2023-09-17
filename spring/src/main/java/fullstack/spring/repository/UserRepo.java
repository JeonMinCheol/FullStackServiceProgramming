package fullstack.spring.repository;

import fullstack.spring.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByNickName(String nickname);
    Optional<User> findByEmail(String userName);
}
