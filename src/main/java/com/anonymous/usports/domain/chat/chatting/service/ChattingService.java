package com.anonymous.usports.domain.chat.chatting.service;

import com.anonymous.usports.domain.chat.chatting.dto.ChattingInput;
import com.anonymous.usports.domain.chat.chatting.dto.ChattingDto;
import java.util.List;

public interface ChattingService {
  ChattingDto save(Long memberId, ChattingInput input);
  List<ChattingDto> getAllChattingByChatRoom(Long chatRoomId);
}
