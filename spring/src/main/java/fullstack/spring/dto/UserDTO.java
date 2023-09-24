package fullstack.spring.dto;

import fullstack.spring.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String email;
    private String name;
    private String nickName;
    private Role role;
}
