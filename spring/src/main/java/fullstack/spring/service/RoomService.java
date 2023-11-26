package fullstack.spring.service;

import fullstack.spring.dto.RoomDTO;
import fullstack.spring.entity.*;
import fullstack.spring.repository.*;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoomService {
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FriendRepo friendRepo;
    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private ProfileRepo profileRepo;


    public ResponseEntity<?> createRoom(HttpServletRequest httpServletRequest, String targetName) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);
        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

       try{
        Friend friend = friendRepo.findByNickNameAndUserId(targetName, user.getId()).orElseThrow(()->new UsernameNotFoundException("친구를 찾을 수 없습니다."));
        if(roomRepo.existsByUserIdAndFriendId(user.getId(), friend.getId()))
           return new ResponseEntity<String>("Already exists.", HttpStatus.ALREADY_REPORTED);

           Room room = Room
                   .builder()
                   .user(user)
                   .friend(friend)
                   .target(friend.getTargetId())
                   .build();

           roomRepo.save(room);

           return new ResponseEntity<String>(String.valueOf(room.getId()), HttpStatusCode.valueOf(200));
       }catch (Exception e){
           log.info(e.getMessage());
           return null;
       }
    }

    public ResponseEntity<?> getRooms(HttpServletRequest httpServletRequest) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

        // user는 현재 서비스를 요청한 유저
        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        List<RoomDTO> rooms = new ArrayList<>();

        // 내가 생성한 경우 찾기 위해서
        roomRepo.findAllByUserId(user.getId()).get().forEach(room -> {
            Optional<Comment> lastComment = chatRepo.getLastChat(room.getId());

            Optional<Profile> profile = profileRepo.findByUserId(room.getFriend().getTargetId());
            String path = null;

            path = profile.isPresent() ? profile.get().getPath() : "/profileImg/default-profile.jpg";

            if(lastComment.isPresent())
                rooms.add(RoomDTO
                        .builder()
                        .id(room.getId())
                        .user1(room.getUser().getId())
                        .user2(room.getFriend().getTargetId())
                        .lastComment(lastComment.get().getText())
                        .path(path)
                        .nickName(room.getFriend().getNickName())
                        .build());
            else
                rooms.add(RoomDTO
                        .builder()
                        .id(room.getId())
                        .user1(room.getUser().getId())
                        .user2(room.getFriend().getTargetId())
                        .lastComment(null)
                        .path(path)
                        .nickName(room.getFriend().getNickName())
                        .build());
        });

        // TODO : 여기 코드 바꿔야함 (현재 로직과 성립되지 않음)
        // 상대가 생성한 경우 찾기 위해서
        roomRepo.findAllByTarget(user.getId()).get().forEach(room -> {
            Optional<Comment> lastComment = chatRepo.getLastChat(room.getId());

            Optional<Profile> profile = profileRepo.findByUserId(room.getUser().getId());
            String path = null;

            path = profile.isPresent() ? profile.get().getPath() : "/profileImg/default-profile.jpg";

            if(lastComment.isPresent())
                rooms.add(RoomDTO
                    .builder()
                    .id(room.getId())
                    .user1(user.getId())                            // user1은 나
                    .user2(room.getUser().getId())                  // user2는 친구
                    .lastComment(lastComment.get().getText())
                    .path(path)
                    .nickName(room.getUser().getNickName())
                    .build());
            else
                rooms.add(RoomDTO
                        .builder()
                        .id(room.getId())
                        .user1(user.getId())
                        .user2(room.getUser().getId())
                        .lastComment(null)
                        .path(path)
                        .nickName(room.getUser().getNickName())
                        .build());
        });

        return new ResponseEntity<>(rooms, HttpStatusCode.valueOf(200));
    }
}
