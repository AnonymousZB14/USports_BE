package com.anonymous.usports.websocket.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatMessageDto;

public interface ChatService {

  ChatMessageDto assembleEnterChat(ChatMessageDto chat, MemberDto loginMember);

  ChatMessageDto assembleMessage(ChatMessageDto chat, MemberDto loginMember);

  void receiveMessage(ChatMessageDto chatDto);
}