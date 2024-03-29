package fullstack.spring.controller;

import fullstack.spring.dto.ChatDTO;
import fullstack.spring.dto.ChatResponseDTO;
import fullstack.spring.dto.TranslateDataDTO;
import fullstack.spring.entity.Chat;
import fullstack.spring.repository.ChatRepo;
import fullstack.spring.security.service.JwtService;
import fullstack.spring.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api" , produces = "application/json; charset=utf8")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private JwtService jwtService;

    // Postman test용
    @PostMapping("/room/{roomId}/chat")
    public ResponseEntity<?> addChat(HttpServletRequest httpServletRequest, @PathVariable long roomId, @RequestPart TranslateDataDTO comment, @RequestPart(required = false) MultipartFile image) {
        try {
            chatService.addChat_http(httpServletRequest,roomId, comment, image);
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/chat")
    public ResponseEntity<?> getChat(HttpServletRequest httpServletRequest, @PathVariable long roomId) {
        try {
            List<Chat> comments = chatRepo.findAllByUserIdAndRoomId(jwtService.extractIdFromHeader(httpServletRequest), roomId).get();
            List<ChatResponseDTO> response = new ArrayList<>();

            comments.forEach(comment -> response.add(ChatResponseDTO
                    .builder()
                    .nickName(comment.getUser().getNickName())
                    .text(comment.getText())
                    .translate(comment.getTranslate())
                    .image(comment.getImage())
                    .build()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/chats")
    public ResponseEntity<?> getChats(HttpServletRequest httpServletRequest, @PathVariable long roomId) {
        try {
            List<Chat> comments = chatRepo.findAllByRoomId(roomId).get();
            List<ChatResponseDTO> response = new ArrayList<>();

            comments.forEach(comment -> response.add(ChatResponseDTO
                    .builder()
                    .nickName(comment.getUser().getNickName())
                    .text(comment.getText())
                    .translate(comment.getTranslate())
                    .image(comment.getImage())
                    .time(comment.getCreatedDate().toString())
                    .build()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
