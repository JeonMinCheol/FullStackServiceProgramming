package fullstack.spring.security.service;

import fullstack.spring.entity.ImageType;
import fullstack.spring.entity.Profile;
import fullstack.spring.entity.Role;
import fullstack.spring.entity.User;
import fullstack.spring.repository.ProfileRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.dto.LoginDto;
import fullstack.spring.service.MediaService;
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

    // 1. 회원 가입
    // 이메일과 닉네임 중복 확인
    // 중복 시 에러

    public ResponseEntity<?> register(User request, MultipartFile Avatar) throws Exception {

        log.info(String.valueOf(request.getId()));
        log.info(String.valueOf(request.getNickName()));
        log.info(String.valueOf(request.getEmail()));

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

        if(Avatar != null) {
            Profile profile = Profile
                    .builder()
                    .path(mediaService.uploadImg(Avatar, ImageType.profileImg))
                    .user(user)
                    .build();

            profileRepo.save(profile);
        }
        return new ResponseEntity<String>("Register has been successfully finished.", HttpStatus.CREATED);
    }

    public ResponseEntity<?> login(LoginDto request) throws Exception {
        String email = request.getEmail();
        String password = request.getPassword();

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getStatus())
                throw new Exception("이미 로그인 되어있습니다.");

            else if(!passwordEncoder.matches(password, user.getPassword()))
                throw new Exception("비밀번호가 일치하지 않습니다.");

            user.setStatus(true);
            userRepo.save(user);
        }
        else {
            throw new Exception("등록되지 않은 이메일입니다.");
        }

        return ResponseEntity.ok(jwtService.generateToken(email));
    }

    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) throws Exception {
        long id = jwtService.extractIdFromHeader(httpServletRequest);
        Optional<User> optionalUser = userRepo.findById(id);

        if(optionalUser.isEmpty())
            throw new Exception("등록되지 않은 이메일입니다.");

        User user = optionalUser.get();

        if(!user.getStatus())
            throw new Exception("이미 로그아웃 되어있습니다.");

        user.setStatus(false);
        userRepo.save(user);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    public ResponseEntity<?> getUsers() {
        log.info(userRepo.findAll().toString());
        return ResponseEntity.ok(userRepo.findAll());
    }
}
