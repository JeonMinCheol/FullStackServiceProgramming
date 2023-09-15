package fullstack.spring.security.dto;

import lombok.*;

@Data
@Builder
public class LoginDto {
    private final String email;
    private final String password;
}
