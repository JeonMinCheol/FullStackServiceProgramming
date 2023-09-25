package fullstack.spring.controller;

import fullstack.spring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api" , produces = "application/json; charset=utf8")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{nickName}")
    public ResponseEntity<?> getUserInfo(HttpServletRequest httpServletRequest, @PathVariable String nickName) {
        try {
            return userService.findUser(httpServletRequest, nickName);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
