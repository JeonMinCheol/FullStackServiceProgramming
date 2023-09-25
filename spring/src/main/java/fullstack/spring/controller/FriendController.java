package fullstack.spring.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fullstack.spring.security.service.JwtService;
import fullstack.spring.service.FriendService;
import fullstack.spring.service.RoomService;
import fullstack.spring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api" , produces = "application/json; charset=utf8")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/friend")
    public ResponseEntity<?> getFriendList(HttpServletRequest httpServletRequest) {
        try {
            return friendService.getFriends(httpServletRequest);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("friend/{friendName}")
    public ResponseEntity<?> addFriend(HttpServletRequest httpServletRequest, @PathVariable String friendName) {
        try {
            friendService.addFriend(httpServletRequest, friendName);

            return roomService.createRoom(httpServletRequest, friendName);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatusCode.valueOf(403));
        }
    }


}
