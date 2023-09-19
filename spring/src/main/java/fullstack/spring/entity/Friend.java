package fullstack.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "friend")
@Getter
@Setter
@AllArgsConstructor
public class Friend extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "friend")
    private List<UserFriend> friends; // user_id
}
