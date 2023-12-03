package fullstack.spring.service;

import fullstack.spring.dto.FriendDTO;
import fullstack.spring.dto.UserDTO;
import fullstack.spring.entity.Friend;
import fullstack.spring.entity.Profile;
import fullstack.spring.entity.User;
import fullstack.spring.repository.FriendRepo;
import fullstack.spring.repository.ProfileRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProfileRepo profileRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FriendRepo friendRepo;

    public ResponseEntity<?> findUser(HttpServletRequest httpServletRequest, String nickName) throws Exception {
        try{
            // 친구
            User user = userRepo.findByNickName(nickName).get();

            // 친구 프로필
            Optional<Profile> profile = profileRepo.findByUserId(user.getId());


            // 나
            long id = jwtService.extractIdFromHeader(httpServletRequest);

            if(id == user.getId())
                return new ResponseEntity<>("Target is current user.", HttpStatus.BAD_REQUEST);

            String path = null;

            path = profile.isPresent() ? profile.get().getPath() : "/profileImg/default-profile.jpg";
            UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getNickName(), path, user.getRole());

            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("user not founded.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> findUserByToken(HttpServletRequest httpServletRequest) throws Exception {
        try{
            long id = jwtService.extractIdFromHeader(httpServletRequest);
            User user = userRepo.findById(id).get();

            String path = null;

            Optional<Profile> profile = profileRepo.findByUserId(id);

            path = profile.isPresent() ? profile.get().getPath() : "/profileImg/default-profile.jpg";
            UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getNickName(), path, user.getRole());

            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("user not founded.", HttpStatus.BAD_REQUEST);
        }
    }
}
