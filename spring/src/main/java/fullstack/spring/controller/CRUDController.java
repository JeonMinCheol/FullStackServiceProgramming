package fullstack.spring.controller;

import fullstack.spring.security.service.JwtService;
import fullstack.spring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
public class CRUDController {
    @Autowired
    private UserService userService;

    private JwtService jwtService;

    @GetMapping("/friend")
    public ResponseEntity<?> getFriendList(HttpServletRequest httpServletRequest) {
        try {
            return userService.getFriends(httpServletRequest);
        } catch (Exception e) {
            return ResponseEntity.ok("실패");
        }
    }

    @PostMapping("friend/{friendName}")
    public ResponseEntity<?> makeFriendRelationship (HttpServletRequest httpServletRequest, @PathVariable String friendName) {
        try {
            return userService.makeFriendRelationship(httpServletRequest, friendName);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatusCode.valueOf(403));
        }
    }
}
