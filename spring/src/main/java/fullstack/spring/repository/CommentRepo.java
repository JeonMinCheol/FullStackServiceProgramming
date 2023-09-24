package fullstack.spring.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fullstack.spring.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByUserIdAndRoomId(long user, long room);
    Optional<List<Comment>> findAllByRoomId(long room);
}
