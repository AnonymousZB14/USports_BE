package com.anonymous.usports.websocket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // message 브로커를 활성화 시키는 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메세지 구독 url
        config.enableStompBrokerRelay("/exchange") //메시지 수신(subscribe), 경로를 설정해주는 메서드
            .setRelayHost("localhost")
            .setRelayPort(61613) //RabbitMQ STOMP 기본 포트
            .setClientLogin("rootuser")
            .setClientPasscode("rootpass")
            .setSystemLogin("rootuser")
            .setSystemPasscode("rootpass")
            .setSystemHeartbeatSendInterval(10000)
            .setSystemHeartbeatReceiveInterval(10000);
//        config.setPathMatcher(new AntPathMatcher("."));
        // 메세지 발행 url
        config.setApplicationDestinationPrefixes("/pub"); //메시지 보낼(publish) 경로를 설정
        //config.setUserDestinationPrefix("/users") //특정 사용자에게 메시지 전송 시 사용할 주소
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat") // URL 또는 URI
            .setAllowedOriginPatterns("*")
            .withSockJS(); // 소켓을 지원하지 않는 브라우저라면, sockJS를 사용
    }

}
