package fullstack.spring.dto;

import fullstack.spring.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO implements Serializable {
    private String translate;
    private String nickName;
    private String image;
    private String text;
    private String time;

    public ChatResponseDTO(Chat Chat) {
        this.translate = Chat.getTranslate();
        this.nickName = Chat.getNickName();
        this.image = Chat.getImage();
        this.text = Chat.getText();
        this.time = Chat.getCreatedDate().toString();
    }
}
