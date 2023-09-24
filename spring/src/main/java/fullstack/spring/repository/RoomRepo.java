package fullstack.spring.repository;

import fullstack.spring.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepo extends JpaRepository<Room, Long> {
    boolean existsByUserIdAndFriendId(long userId, long friendId);
    Optional<Room> findByUserIdAndFriendId(long userId, long FriendId);

    Optional<List<Room>> findAllByUserId(long user);
    Optional<List<Room>> findAllByFriendId(long friend);
}
