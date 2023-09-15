package fullstack.spring.repository;

import fullstack.spring.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room, Long> {


}
