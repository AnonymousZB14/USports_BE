package com.anonymous.usports.domain.mongodbtest;

import java.util.List;

public interface ChattingService {
  ChattingDto save(Long memberId, ChattingInput input);
  List<ChattingDto> getAllChattingByChatRoom(Long chatRoomId);
}
