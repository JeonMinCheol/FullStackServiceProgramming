package fullstack.spring.repository;

import fullstack.spring.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepo extends JpaRepository<Friend, Long> {
    Optional<List<Friend>> findAllByUserId(long id);
    Optional<Friend> findByNickNameAndUserId(String targetName, long userId);
    Optional<Friend> findByTargetId(long id);
    void deleteByUserId(long id);

    Optional<List<Friend>> findAllByTargetId(long id);
}
