package fullstack.spring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
// 단순히 채팅 생성 위치 표시용으로 사용
// 실제 통신은 소켓으로 이루어짐.

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Friend friend;

    private long target;

    @OneToMany(mappedBy = "room")
    private List<Chat> chats;
}
