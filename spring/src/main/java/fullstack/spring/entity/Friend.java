package fullstack.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Friend extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nickName;
    private String email;
    private long friendId;
    private byte profileImg;

    @JsonIgnore
    @ManyToOne
    @ToString.Exclude
    private User user; // user와 연결할 외래키
}
