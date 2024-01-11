package com.anonymous.usports.websocket.service;

import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.dto.ChatMessageResponseDto;

public interface ChatService {

  ChatMessageResponseDto assembleEnterChat(ChatMessageDto chat);

  ChatMessageResponseDto assembleMessage(ChatMessageDto chat);

  void receiveMessage(ChatMessageResponseDto chatDto);
}