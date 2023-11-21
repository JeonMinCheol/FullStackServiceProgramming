package fullstack.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fullstack.spring.dto.ChatRequestDTO;
import fullstack.spring.dto.ChatResponseDTO;
import fullstack.spring.dto.TranslateDataDTO;
import fullstack.spring.entity.Comment;
import fullstack.spring.entity.ImageType;
import fullstack.spring.entity.Room;
import fullstack.spring.entity.User;
import fullstack.spring.repository.ChatRepo;
import fullstack.spring.repository.RoomRepo;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@Service
public class ChatService {
    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private TranslateService translateService;

    @Autowired
    private MediaService mediaService;

    private JSONParser parser = new JSONParser();

    public void addChat_http(HttpServletRequest httpServletRequest, long roomId, TranslateDataDTO content, MultipartFile image) throws Exception {
        String userEmail = jwtService.extractEmailFromHeader(httpServletRequest);

        User user = userRepo.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Room room = roomRepo.findById(roomId).orElseThrow(()->new NullPointerException("방 정보가 존재하지 않습니다."));

        String langCode = translateService.detect(content.getText());
        String translateText = translateService.translate(content.getText(), langCode);

        String imageUrl = (image != null) ? mediaService.uploadImg(image, ImageType.commentImg) : null;

        Comment comment = Comment
                .builder()
                .room(room)
                .user(user)
                .text(content.getText())
                .translate(translateText)
                .image(imageUrl)
                .build();

        chatRepo.save(comment);
    }


    // 웹소켓으로 채팅 전송 시 이 함수를 먼저 호출해서 저장 + 해석 결과를 받음
    // 받은 결과를 그대로 방에 전달하면 끝
    public ChatResponseDTO chattingHandler(ChatRequestDTO chatRequestDTO) throws ParseException, JsonProcessingException {
        String text = chatRequestDTO.getText();
        String userName = chatRequestDTO.getUsername();
        long roomId = chatRequestDTO.getRoomId();
        byte[] binaryImage = chatRequestDTO.getBinaryImage();

        User user = userRepo.findByNickName(userName).orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Room room = roomRepo.findById(roomId).orElseThrow(()->new NullPointerException("방 정보가 존재하지 않습니다."));

        String translateText = null;

        // text 전송 시
        if(text != null) {
            String langCode = translateService.detect(text);
            translateText = translateService.translate(text, langCode);
        }

        // 유저 프로필 이미지는 방 입장 시 받아서 저장해서 사용
        Comment comment = Comment
                .builder()
                .room(room)
                .user(user)
                .text(text)
                .translate(translateText)
                .image(Arrays.toString(binaryImage)) // chat에서 이미지를 base64로 인코딩해서 서버로 전달
                .build();

        chatRepo.save(comment);

        return new ChatResponseDTO(comment);
    }
}
