package fullstack.spring.repository;

import fullstack.spring.entity.Friend;
import fullstack.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepo extends JpaRepository<Friend, Long> {
    //Optional<List<Friend>> findAllByUserId(long uid);
    Optional<Friend> findById(long fid);
    void deleteById(long uid);
}
