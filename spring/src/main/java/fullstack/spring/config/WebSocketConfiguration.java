package fullstack.spring.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fullstack.spring.controller.WebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

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

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(6000 * 1024 * 1024);
        registry.setSendTimeLimit(5 * 10000);
        registry.setSendBufferSizeLimit(6000 * 1024 * 1024);
    }

    @EventListener
    public void connectEvent(SessionConnectEvent sessionConnectEvent){
        System.out.println(sessionConnectEvent);
        System.out.println("유저 연결");
    }
    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        System.out.println(sessionDisconnectEvent.getCloseStatus());
        System.out.println("연결 해제");
    }
}
