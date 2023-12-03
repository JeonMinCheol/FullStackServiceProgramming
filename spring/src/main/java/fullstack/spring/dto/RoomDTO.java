package fullstack.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private long id;
    private long user1;
    private long user2;
    private String nickName;
    private String lastComment;
    private String path;
}
