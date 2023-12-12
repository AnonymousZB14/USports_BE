package com.anonymous.usports.domain.chat.chatting.dto;

import com.anonymous.usports.domain.chat.chatting.entity.ChattingEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChattingInput {
  public Long chatRoomId;
  private String content;

  public static ChattingEntity toEntity(ChattingInput input, MemberEntity member){
    return ChattingEntity.builder()
        .chatRoomId(input.getChatRoomId())
        .memberId(member.getMemberId())
        .name(member.getName())
        .imageAddress(member.getProfileImage())
        .content(input.getContent())
        .createdAt(LocalDateTime.now())
        .build();
  }
}
