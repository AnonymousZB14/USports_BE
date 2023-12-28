package com.anonymous.usports.websocket.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatMessageDto;

public interface ChatService {

  ChatMessageDto assembleEnterChat(ChatMessageDto chat);

  ChatMessageDto assembleMessage(ChatMessageDto chat);

  void receiveMessage(ChatMessageDto chatDto);
}