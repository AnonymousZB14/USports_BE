package com.anonymous.usports.websocket.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.entity.ChattingEntity;
import com.anonymous.usports.websocket.repository.ChattingRepository;
import com.anonymous.usports.websocket.service.ChatService;
import com.anonymous.usports.websocket.type.MessageType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final MemberRepository memberRepository;
  private final ChattingRepository chattingRepository;

  @Override
  public ChatMessageDto assembleEnterChat(ChatMessageDto chat) {

    return ChatMessageDto.builder()
        .chatRoomId(chat.getChatRoomId())
        .userId(chat.getUserId())
        .user(chat.getUser())
        .imageAddress(chat.getImageAddress())
        .time(LocalDateTime.now())
        .type(MessageType.JOIN)
        .content(chat.getUser() + "님이 입장하셨습니다.")
        .build();

  }

  @Override
  public ChatMessageDto assembleMessage(ChatMessageDto chat) {

    return ChatMessageDto.builder()
        .chatRoomId(chat.getChatRoomId())
        .userId(chat.getUserId())
        .user(chat.getUser())
        .imageAddress(chat.getImageAddress())
        .time(LocalDateTime.now())
        .type(MessageType.CHAT)
        .content(chat.getContent())
        .build();

  }

  @Override
  public void receiveMessage(ChatMessageDto chatDto) {

    memberRepository.findById(chatDto.getUserId())
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    ChattingEntity chatting = ChatMessageDto.toEntity(chatDto);
    chattingRepository.save(chatting);
  }

}
