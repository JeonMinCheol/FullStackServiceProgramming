package fullstack.spring.service;

import fullstack.spring.dto.FriendDTO;
import fullstack.spring.entity.Friend;
import fullstack.spring.entity.Profile;
import fullstack.spring.entity.User;
import fullstack.spring.repository.ChatRepo;
import fullstack.spring.repository.FriendRepo;
import fullstack.spring.repository.ProfileRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FriendService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FriendRepo friendRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private ProfileRepo profileRepo;
    @Autowired
    private ChatRepo chatRepo;
    public void addFriend(HttpServletRequest httpServletRequest, String targetName) throws RuntimeException, IOException, ServletException {
        try{
            String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

            User user1 = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            User user2 = userRepo.findByNickName(targetName).orElseThrow(()->new UsernameNotFoundException("친구를 찾을 수 없습니다."));

            Friend friend1 = Friend
                    .builder()
                    .user(user1)
                    .email(user2.getEmail())
                    .nickName(user2.getNickName())
                    .targetId(user2.getId())
                    .build();

            Friend friend2 = Friend
                    .builder()
                    .user(user2)
                    .email(user1.getEmail())
                    .nickName(user1.getNickName())
                    .targetId(user1.getId())
                    .build();

            for (Friend friend : friendRepo.findAllByUserId(user1.getId()).get()) {
                if(Objects.equals(friend.getTargetId(), user2.getId())) {
                    log.info(friend.getEmail() + friend1.getEmail());
                    throw new RuntimeException("이미 등록된 친구입니다.");
                }
            }

            friendRepo.save(friend1);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (IOException e){
            throw new IOException(e);
        } catch (ServletException e) {
            throw new ServletException(e);
        }
    }

    public ResponseEntity<?> getFriends(HttpServletRequest httpServletRequest) throws Exception {
        try{
            long id = jwtService.extractIdFromHeader(httpServletRequest);
            String lastComment = "";

            List<FriendDTO> response = new ArrayList<>();

            friendRepo.findAllByUserId(id).get().forEach(friend -> {
                Optional<Profile> profile = profileRepo.findByUserId(friend.getUser().getId());
                String path = null;

                path = profile.isPresent() ? profile.get().getPath() : "/profileImg/default-profile.jpg";

                response.add(FriendDTO
                        .builder()
                        .id(friend.getId())
                        .nickName(friend.getNickName())
                        .path(path)
                        .email(friend.getEmail())
                        .targetId(friend.getTargetId())
                        .build()
                );
            });

            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
