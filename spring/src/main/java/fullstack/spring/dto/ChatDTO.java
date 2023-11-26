package fullstack.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// http 테스트 용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private String translate;
    private String username;
    private String image;
    private String text;
}
