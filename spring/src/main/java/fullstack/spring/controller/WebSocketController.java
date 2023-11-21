package fullstack.spring.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import fullstack.spring.dto.ChatRequestDTO;
import fullstack.spring.dto.ChatResponseDTO;
import fullstack.spring.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat")
    public void sendTo(ChatRequestDTO chatRequestDTO) throws ParseException, JsonProcessingException {
        long roomId = chatRequestDTO.getRoomId();
        // 데이터 저장 및 해석.
        ChatResponseDTO chatResponseDTO = chatService.chattingHandler(chatRequestDTO);

        // 메세지 전송 (이미지는 바이너리로 변환)
        simpMessageSendingOperations.convertAndSend("/sub/room/"+roomId, chatResponseDTO);
    }
}
