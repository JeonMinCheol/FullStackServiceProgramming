package fullstack.spring.security.controller;

import fullstack.spring.entity.User;
import fullstack.spring.security.dto.LoginDto;
import fullstack.spring.security.service.AuthenticationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.sql.SQLException;

@Slf4j
@EnableWebMvc
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request, @RequestPart(required = false) MultipartFile Avatar) {
        try{
            return authenticationService.register(request, Avatar);
        }
        catch (SQLException e) {
            return new ResponseEntity<String>(e.getMessage(),HttpStatusCode.valueOf(403));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto request) {
        try{
            return authenticationService.login(request);
        } catch(Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatusCode.valueOf(403));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUser() {
        try{
            return authenticationService.getUsers();
        } catch (Exception e) {
            return new ResponseEntity<String>("error", HttpStatusCode.valueOf(403));
        }
    }
}
