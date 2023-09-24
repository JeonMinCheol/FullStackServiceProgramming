package fullstack.spring.repository;

import fullstack.spring.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(long id);

    boolean existsByUserId(long id);
}
