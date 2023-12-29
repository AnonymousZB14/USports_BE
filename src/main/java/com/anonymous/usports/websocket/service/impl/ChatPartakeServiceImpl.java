package com.anonymous.usports.websocket.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.constant.ChatConstant;
import com.anonymous.usports.global.exception.ChatException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.websocket.dto.MarkAsReadRequestDto;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import com.anonymous.usports.websocket.entity.ChattingEntity;
import com.anonymous.usports.websocket.repository.ChatPartakeRepository;
import com.anonymous.usports.websocket.repository.ChatRoomRepository;
import com.anonymous.usports.websocket.repository.ChattingRepository;
import com.anonymous.usports.websocket.service.ChatPartakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatPartakeServiceImpl implements ChatPartakeService {

  private final ChatPartakeRepository chatPartakeRepository;
  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChattingRepository chattingRepository;

  // 마지막으로 읽은 채팅 Id 체크
  @Transactional
  public MarkAsReadRequestDto.Response markChatAsRead(Long chatRoomId, Long memberId) {
    ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));

    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    ChatPartakeEntity chatPartake = chatPartakeRepository.findByChatRoomEntityAndMemberEntity(chatRoom, member)
        .orElseThrow(() -> new ChatException(ErrorCode.USER_NOT_IN_THE_CHAT));

    ChattingEntity chattingEntity = chattingRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoomId);

    chatPartake.setLastReadChatId(chattingEntity.getId().toString());

    chatPartakeRepository.save(chatPartake);

    return MarkAsReadRequestDto.Response.builder()
        .message(ChatConstant.MARK_READ_CHAT)
        .build();
  }
}
