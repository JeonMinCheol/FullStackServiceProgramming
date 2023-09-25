package fullstack.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Friend extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nickName;
    private String email;
    private long targetId;    // 친구의 userid

    @JsonIgnore
    @ManyToOne
    @ToString.Exclude
    private User user; // user와 연결할 외래키

    @JsonIgnore
    @OneToMany(mappedBy = "friend")
    private List<Room> room;
}
