package com.anonymous.usports.domain.chatting.service;

import com.anonymous.usports.domain.chatting.dto.ChattingDto;
import com.anonymous.usports.domain.chatting.dto.ChattingInput;
import java.util.List;

public interface ChattingService {
  ChattingDto save(Long memberId, ChattingInput input);
  List<ChattingDto> getAllChattingByChatRoom(Long chatRoomId);
}
