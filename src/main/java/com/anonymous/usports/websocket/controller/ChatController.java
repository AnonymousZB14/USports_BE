package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.websocket.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    /**
     * 메세지를 보낼 때
     * @SendTo() : which topic or queue you want to send the message (Message Broker)
     * /topic/public 을 통해서 채팅에 구독되어 있는 모든 사람들이 메세지를 접근할 수 있다
     * @PayLoad : payload라서 SendTo()에 적힌 곳으로 메세지 넘어간다
     */
    @MessageMapping("/chat/message")
    @SendTo("/sub/public")
    public ChatMessageDto sendMessage(
            @Payload ChatMessageDto chatMessageDto
    ) {
         return chatMessageDto;
    }

    /**
     * 유저와 웹 소켓이 연결될 때에 생성되는 메세지다
     */
    @MessageMapping("/chat/add-user")
    @SendTo("/sub/public")
    public ChatMessageDto addUser(
        @Payload ChatMessageDto chatMessageDto,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in WebSocket session
        headerAccessor.getSessionAttributes().put("username", chatMessageDto.getSender());
        return chatMessageDto;
    }
}
