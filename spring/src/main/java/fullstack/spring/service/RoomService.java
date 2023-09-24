package fullstack.spring.service;

import fullstack.spring.dto.RoomDTO;
import fullstack.spring.entity.Friend;
import fullstack.spring.entity.Room;
import fullstack.spring.entity.User;
import fullstack.spring.repository.FriendRepo;
import fullstack.spring.repository.RoomRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public ResponseEntity<?> createRoom(HttpServletRequest httpServletRequest, String targetName) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

        User user1 = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Friend friend = friendRepo.findByNickName(targetName).orElseThrow(()->new UsernameNotFoundException("친구를 찾을 수 없습니다."));

        if(roomRepo.existsByUserIdAndFriendId(user1.getId(), friend.getId()))
            throw new Exception("방이 이미 존재합니다.");

        Room room = Room
                .builder()
                .user(user1)
                .friend(friend)
                .build();

        roomRepo.save(room);

        return new ResponseEntity<String>(String.valueOf(room.getId()), HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> getRooms(HttpServletRequest httpServletRequest) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);
        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Friend friend = friendRepo.findByTargetId(user.getId()).get();

        List<RoomDTO> rooms = new ArrayList<>();

        // 내가 생성한 경우 찾기 위해서
        roomRepo.findAllByUserId(user.getId()).get().forEach(room -> {
            rooms.add(RoomDTO
                    .builder()
                            .id(room.getId())
                            .user1(room.getUser().getId())
                            .user2(room.getFriend().getTargetId())
                    .build());
        });

        // 상대가 생성한 경우 찾기 위해서
        roomRepo.findAllByFriendId(friend.getId()).get().forEach(room -> {
            rooms.add(RoomDTO
                    .builder()
                    .id(room.getId())
                    .user1(room.getUser().getId())
                    .user2(room.getFriend().getTargetId())
                    .build());
        });

        return new ResponseEntity<>(rooms, HttpStatusCode.valueOf(200));
    }
}
