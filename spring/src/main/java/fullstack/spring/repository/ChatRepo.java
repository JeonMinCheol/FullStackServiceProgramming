package fullstack.spring.repository;

import fullstack.spring.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    Optional<List<Chat>> findAllByUserIdAndRoomId(long user, long room);
    Optional<List<Chat>> findAllByRoomId(long room);

    @Query(value = "SELECT * FROM CHAT WHERE room_id = :roomId ORDER BY id DESC LIMIT 1;", nativeQuery = true)
    Optional<Chat> getLastChat(@Param("roomId") long roomId);


}
