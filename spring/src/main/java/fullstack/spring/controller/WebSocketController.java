package fullstack.spring.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import fullstack.spring.dto.ChatRequestDTO;
import fullstack.spring.dto.ChatResponseDTO;
import fullstack.spring.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.io.IOException;

@Log
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat")
    public void sendTo(ChatRequestDTO chatRequestDTO) throws ParseException, IOException {
        long roomId = chatRequestDTO.getRoomId();
        // 데이터 저장 및 해석.
        ChatResponseDTO chatResponseDTO = chatService.chattingHandler(chatRequestDTO);
        // 메세지 전송
        simpMessageSendingOperations.convertAndSend("/sub/room/"+roomId, chatResponseDTO);
    }
}
