package fullstack.spring.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fullstack.spring.dto.CommentDTO;
import fullstack.spring.dto.TranslateDataDTO;
import fullstack.spring.entity.Comment;
import fullstack.spring.repository.CommentRepo;
import fullstack.spring.security.service.JwtService;
import fullstack.spring.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api" , produces = "application/json; charset=utf8")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/room/{roomId}/comment")
    public ResponseEntity<?> addComment(HttpServletRequest httpServletRequest, @PathVariable long roomId, @RequestBody TranslateDataDTO comment) {
        try {
            commentService.addComment(httpServletRequest,roomId, comment);
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/comment")
    public ResponseEntity<?> getComment(HttpServletRequest httpServletRequest, @PathVariable long roomId) {
        try {
            List<Comment> comments = commentRepo.findAllByUserIdAndRoomId(jwtService.extractIdFromHeader(httpServletRequest), roomId).get();
            List<CommentDTO> response = new ArrayList<>();

            comments.forEach(comment -> {
                response.add(CommentDTO
                        .builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .text(comment.getText())
                        .translate(comment.getTranslate())
                        .build());
            });
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/comments")
    public ResponseEntity<?> getComments(HttpServletRequest httpServletRequest, @PathVariable long roomId) {
        try {
            List<Comment> comments = commentRepo.findAllByRoomId(roomId).get();
            List<CommentDTO> response = new ArrayList<>();

            comments.forEach(comment -> {
                response.add(CommentDTO
                        .builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .text(comment.getText())
                        .translate(comment.getTranslate())
                        .build());
            });
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
