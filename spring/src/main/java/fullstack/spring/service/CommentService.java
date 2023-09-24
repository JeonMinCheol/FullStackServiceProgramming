package fullstack.spring.service;

import fullstack.spring.dto.TranslateDataDTO;
import fullstack.spring.entity.Comment;
import fullstack.spring.entity.Room;
import fullstack.spring.entity.User;
import fullstack.spring.repository.CommentRepo;
import fullstack.spring.repository.RoomRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class CommentService {
    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private TranslateService translateService;

    private JSONParser parser = new JSONParser();

    public void addComment(HttpServletRequest httpServletRequest, long roomId, TranslateDataDTO content) throws ServletException, IOException, ParseException {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Room room = roomRepo.findById(roomId).orElseThrow(()->new NullPointerException("방 정보가 존재하지 않습니다."));

        String langCode = translateService.detect(content.getText());
        String translateText = translateService.translate(content.getText(), langCode);

        Comment comment = Comment
                .builder()
                .room(room)
                .user(user)
                .text(content.getText())
                .translate(translateText)
                .build();

        commentRepo.save(comment);
    }
}
