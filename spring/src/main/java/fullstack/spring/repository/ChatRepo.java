package fullstack.spring.repository;

import fullstack.spring.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByUserIdAndRoomId(long user, long room);
    Optional<List<Comment>> findAllByRoomId(long room);

    @Query(value = "SELECT * FROM comment WHERE room_id = :roomId ORDER BY id DESC LIMIT 1;", nativeQuery = true)
    Optional<Comment> getLastChat(@Param("roomId") long roomId);


}
