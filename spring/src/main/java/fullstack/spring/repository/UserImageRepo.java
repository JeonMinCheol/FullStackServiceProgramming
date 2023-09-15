package fullstack.spring.repository;

import fullstack.spring.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepo extends JpaRepository<UserImage, Long> {
}
