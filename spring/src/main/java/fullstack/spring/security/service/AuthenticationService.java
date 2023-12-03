package fullstack.spring.security.service;

import fullstack.spring.entity.*;
import fullstack.spring.repository.FriendRepo;
import fullstack.spring.repository.ProfileRepo;
import fullstack.spring.repository.RoomRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.dto.LoginDto;
import fullstack.spring.service.FriendService;
import fullstack.spring.service.MediaService;
import fullstack.spring.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ProfileRepo profileRepo;
    private final MediaService mediaService;
    private final FriendRepo friendRepo;
    private final RoomRepo roomRepo;

    // 1. 회원 가입
    // 이메일과 닉네임 중복 확인
    // 중복 시 에러

    public ResponseEntity<?> register(User request, MultipartFile image) throws Exception {
        if(userRepo.findByEmail(request.getEmail()).isPresent())
            throw new SQLException("이미 등록된 이메일입니다");
        else if(userRepo.findByNickName(request.getNickName()).isPresent()){

            throw new SQLException("이미 등록된 이름입니다.");
        }

        User user = User
                .builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickName(request.getNickName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(false)
                .build();

        userRepo.save(user);

        if(image != null) {
            Profile profile = Profile
                    .builder()
                    .path(mediaService.uploadImg(image, ImageType.profileImg))
                    .user(user)
                    .build();

            profileRepo.save(profile);
            log.info("profile 생성");
        }

        // 나 자신과 친구 추가
        Friend friend = Friend
                .builder()
                .nickName(request.getNickName())
                .email(request.getEmail())
                .user(user)
                .targetId(user.getId())
                .build();

        friendRepo.save(friend);

        // 채팅 방 생성
        Room room = Room
                .builder()
                .friend(friend)
                .user(user)
                .target(user.getId())
                .build();

        roomRepo.save(room);

        return new ResponseEntity<String>("Register has been successfully finished.", HttpStatus.CREATED);
    }

    public ResponseEntity<?> login(LoginDto request) throws Exception {
        String email = request.getEmail();
        String password = request.getPassword();

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getStatus())
                throw new Exception("[Error]: User was already logined.");

            else if(!passwordEncoder.matches(password, user.getPassword()))
                throw new Exception("[Error]:Password was not matched.");

            user.setStatus(true);
            userRepo.save(user);
        }
        else {
            throw new Exception("[Error]:Email was not matched.");
        }

        return ResponseEntity.ok(jwtService.generateToken(email));
    }

    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) throws Exception {
        long id = jwtService.extractIdFromHeader(httpServletRequest);
        Optional<User> optionalUser = userRepo.findById(id);

        if(optionalUser.isEmpty())
            throw new Exception("[Error]: Email was not matched");

        User user = optionalUser.get();

        if(!user.getStatus())
            throw new Exception("[Error]: User was already logouted.");

        user.setStatus(false);
        userRepo.save(user);
        return ResponseEntity.ok("User was succesfully logouted.");
    }

    public ResponseEntity<?> getUsers() {
        log.info(userRepo.findAll().toString());
        return ResponseEntity.ok(userRepo.findAll());
    }
}
