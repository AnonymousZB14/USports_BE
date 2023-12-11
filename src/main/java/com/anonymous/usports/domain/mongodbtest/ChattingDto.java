package com.anonymous.usports.domain.mongodbtest;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChattingDto {

  private String id;

  private Long chatRoomId;

  private Long memberId; //회원 Id

  private String name; //회원 이름

  private String imageAddress;

  private String content; //내용

  private LocalDateTime createdAt; //입력 시간

  public static ChattingDto fromEntity(ChattingEntity chatting){
    return ChattingDto.builder()
        .id(chatting.getId())
        .chatRoomId(chatting.getChatRoomId())
        .memberId(chatting.getMemberId())
        .name(chatting.getName())
        .imageAddress(chatting.getImageAddress())
        .content(chatting.getContent())
        .createdAt(chatting.getCreatedAt())
        .build();
  }
}
