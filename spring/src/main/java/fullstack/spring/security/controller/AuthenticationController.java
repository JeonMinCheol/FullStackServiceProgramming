package fullstack.spring.security.controller;

import fullstack.spring.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestPart String request, @RequestPart(required = false) MultipartFile Avatar) {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String request) {
        return null;
    }

}
