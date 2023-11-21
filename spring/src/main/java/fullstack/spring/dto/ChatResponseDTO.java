package fullstack.spring.dto;

import fullstack.spring.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO implements Serializable {
    private String translate;
    private String username;
    private String image;
    private String text;

    public ChatResponseDTO(Comment comment) {
        this.translate = comment.getTranslate();
        this.username = comment.getUser().getUsername();
        this.image = comment.getImage();
        this.text = comment.getText();
    }
}
