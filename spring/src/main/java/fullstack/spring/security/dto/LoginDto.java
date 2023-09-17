package fullstack.spring.security.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class LoginDto {
    private String email;
    private String password;
}
