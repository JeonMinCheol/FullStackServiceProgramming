package fullstack.spring.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fullstack.spring.controller.WebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Log
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // localhost:5400/ws 연결
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 일단 돌아가는 거 확인하고 적용 예정
//        registry.enableStompBrokerRelay("/sub")
//                .setRelayHost("localhost")
//                .setRelayPort(9092);
        registry.enableSimpleBroker("/sub");

        // 메세지 발행
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
