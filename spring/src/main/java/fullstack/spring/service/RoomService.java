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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public ResponseEntity<?> createRoom(HttpServletRequest httpServletRequest, String targetName) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);
        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

       try{
           //TODO : 오류 발생 지점 (친구가 여러명이네 생각해보니까)
        Friend friend = friendRepo.findByNickNameAndUserId(targetName, user.getId()).orElseThrow(()->new UsernameNotFoundException("친구를 찾을 수 없습니다."));
        if(roomRepo.existsByUserIdAndFriendId(user.getId(), friend.getId()))
           throw new Exception("방이 이미 존재합니다.");

           Room room = Room
                   .builder()
                   .user(user)
                   .friend(friend)
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
        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // TODO : 여기 수정해야함 (현재 친구가 여러명이라서 적절하게 수정할 필요가 있다.)
        List<Friend> friends = friendRepo.findAllByTargetId(user.getId()).get();

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

        // TODO : 여기 코드 바꿔야함 (현재 로직과 성립되지 않음)
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
