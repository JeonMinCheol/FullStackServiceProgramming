package fullstack.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO implements Serializable {
    private String username;
    private byte[] binaryImage;
    private String text;
    private long roomId;
}
