package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.websocket.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations template;

    /**
     * 메세지를 보낼 때
     *
     * @SendTo() : which topic or queue you want to send the message (Message Broker)
     * /topic/public 을 통해서 채팅에 구독되어 있는 모든 사람들이 메세지를 접근할 수 있다
     * @PayLoad : payload라서 SendTo()에 적힌 곳으로 메세지 넘어간다
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(
            ChatMessageDto chat
    ) {
        log.info("Chat {}", chat);
        chat.setContent(chat.getContent());
        template.convertAndSend("/sub/chat/room/" + chat.getChatRoomId(), chat);
    }

    /**
     * 유저와 웹 소켓이 연결될 때에 생성되는 메세지다
     */
    @MessageMapping("/chat/enter")
    public void addUser(
            ChatMessageDto chat
    ) {
        log.info("{} 입장", chat.getSender());
        chat.setContent(chat.getSender() + "님이 입장하셨습니다");
        template.convertAndSend("/sub/chat/room/" + chat.getChatRoomId(), chat);
    }
}
