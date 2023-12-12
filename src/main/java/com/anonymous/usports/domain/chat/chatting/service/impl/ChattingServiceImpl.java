package com.anonymous.usports.domain.chat.chatting.service.impl;

import com.anonymous.usports.domain.chat.chatting.dto.ChattingInput;
import com.anonymous.usports.domain.chat.chatting.service.ChattingService;
import com.anonymous.usports.domain.chat.chatting.dto.ChattingDto;
import com.anonymous.usports.domain.chat.chatting.entity.ChattingEntity;
import com.anonymous.usports.domain.chat.chatting.repository.ChattingRepository;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingServiceImpl implements ChattingService {

  private final MemberRepository memberRepository;
  private final ChattingRepository chattingRepository;

  @Override
  public ChattingDto save(Long memberId, ChattingInput input) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    ChattingEntity saved = chattingRepository.save(ChattingInput.toEntity(input, memberEntity));

    log.info("saved : {}", saved);
    return ChattingDto.fromEntity(saved);
  }

  @Override
  public List<ChattingDto> getAllChattingByChatRoom(Long chatRoomId) {
    List<ChattingEntity> list = chattingRepository.findAllByChatRoomId(chatRoomId);
    List<ChattingDto> result = list.stream().map(ChattingDto::fromEntity)
        .collect(Collectors.toList());
    return result;
  }
}
