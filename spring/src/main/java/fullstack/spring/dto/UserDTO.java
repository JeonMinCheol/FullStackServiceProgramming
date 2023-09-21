package fullstack.spring.dto;

import fullstack.spring.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String email;
    private String name;
    private String nickName;
    private Role role;
}
