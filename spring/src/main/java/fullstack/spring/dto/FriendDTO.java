package fullstack.spring.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class FriendDTO {
    private long id;
    private String nickName;
    private String email;
    private long targetId;    // 친구의 userid
    private String path;
}

