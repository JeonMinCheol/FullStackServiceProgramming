package fullstack.spring.service;

import fullstack.spring.entity.Friend;
import fullstack.spring.entity.User;
import fullstack.spring.repository.FriendRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private UserRepo userRepo;
    private FriendRepo friendRepo;
    private JwtService jwtService;

    public ResponseEntity<?> makeFriendRelationship(HttpServletRequest httpServletRequest, String targetName) {
        try{
            String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

            User user1 = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            User user2 = userRepo.findByNickName(targetName).orElseThrow(()->new UsernameNotFoundException("친구를 찾을 수 없습니다."));

            friendRepo.save(Friend
                    .builder()
                            .user(user1)
                            .email(user2.getEmail())
                            .nickName(user2.getNickName())
                            .friendId(user2.getId())
                    .build());

            friendRepo.save(Friend
                    .builder()
                    .user(user2)
                    .email(user1.getEmail())
                    .nickName(user1.getNickName())
                    .friendId(user1.getId())
                    .build());

            return new ResponseEntity<String>("친구 추가가 완료되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getFriends(HttpServletRequest httpServletRequest) throws Exception {
        try{
            long id = jwtService.extractIdFromHeader(httpServletRequest);

            List<Friend> response = new ArrayList<>();

            return new ResponseEntity<>(friendRepo.findAllByUserId(id).get(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
