package fullstack.spring.security.service;

import fullstack.spring.entity.Role;
import fullstack.spring.entity.User;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.dto.LoginDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NameNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 1. 회원 가입
    // 이메일과 닉네임 중복 확인
    // 중복 시 에러

    public ResponseEntity<?> register(User request, MultipartFile Avatar) throws SQLException {

        if(userRepo.findByEmail(request.getEmail()).isPresent())
            throw new SQLException("이미 등록된 이메일입니다");
        else if(userRepo.findByNickName(request.getNickName()).isPresent())
            throw new SQLException("이미 등록된 이름입니다.");


        User user = User
                .builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickName(request.getNickName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepo.save(user);

        return new ResponseEntity<String>("Register has been successfully finished.", HttpStatus.CREATED);
    }

    public ResponseEntity<?> login(LoginDto request) throws Exception {
        String email = request.getEmail();
        String password = request.getPassword();

        Optional<User> user = userRepo.findByEmail(email);
        if(user.isPresent()) {
            if(!passwordEncoder.matches(password, user.get().getPassword())) {
                throw new Exception("비밀번호가 일치하지 않습니다.");
            }
        }
        else {
            throw new Exception("등록되지 않은 이메일입니다.");
        }

        return ResponseEntity.ok(jwtService.generateToken(email));
    }
}
