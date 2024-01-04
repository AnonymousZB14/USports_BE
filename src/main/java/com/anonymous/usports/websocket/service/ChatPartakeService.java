package com.anonymous.usports.websocket.service;

import com.anonymous.usports.websocket.dto.MarkAsReadRequestDto;

public interface ChatPartakeService {

  MarkAsReadRequestDto.Response markChatAsRead(Long chatRoomId, Long memberId);
}
