package fullstack.spring.security.dto;

import fullstack.spring.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
public class RegisterDto {
    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private final String nickName;
    private final Role role;
}
