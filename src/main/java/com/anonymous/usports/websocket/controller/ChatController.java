package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.global.constant.ChatConstant;
import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.entity.ChattingEntity;
import com.anonymous.usports.websocket.repository.ChattingRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

  private final RabbitTemplate rabbitTemplate;
  private final ChattingRepository chattingRepository;


  // /pub/chat.message.{roomId}로 요청하면 브로커를 통해 처리
  // /exchange/chat.exchange/room.{roomId}를 구동한 클라이언트에 메시지가 전송된다.
  @MessageMapping("chat/enter/{chatRoomId}")
  public void enterUser(@Payload ChatMessageDto chat, @DestinationVariable Long chatRoomId) {

    //TODO 채팅방에 유저 추가하는 메서드 동작

    chat.setTime(LocalDateTime.now());
    chat.setContent(chat.getSenderName() + "님이 입장하셨습니다.");
    rabbitTemplate.convertAndSend(ChatConstant.CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
  }

  @MessageMapping("chat/message/{chatRoomId}")
  public void sendMessage(@Payload ChatMessageDto chat, @DestinationVariable Long chatRoomId) {
    log.info("CHAT ()", chat);
    chat.setTime(LocalDateTime.now());
    chat.setContent(chat.getContent()); //TODO 이게 왜 필요한거지?
    rabbitTemplate.convertAndSend(ChatConstant.CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
  }

  // 기본적으로 chat.queue가 exchange에 바인딩 되어있기 때문에 모든 메시지 처리
  @RabbitListener(queues = ChatConstant.CHAT_QUEUE_NAME)
  public void receive(ChatMessageDto chatDto) {
    log.info("received: " + chatDto.getContent());
    ChattingEntity chatting = ChatMessageDto.toEntity(chatDto);
    chattingRepository.save(chatting);
  }

}