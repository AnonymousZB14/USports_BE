package com.anonymous.usports.domain.chat.chatting.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(collection = "chatting")
public class ChattingEntity {

  @Id//Id 자동 생성
  private String id;

  @Indexed//해당 컬럼으로 인덱싱 -> 이 컬럼으로 검색 속도 빨라짐
  private Long chatRoomId;

  private Long memberId; //회원 Id

  private String name; //회원 이름

  private String imageAddress;

  private String content; //내용

  private LocalDateTime createdAt; //입력 시간
}
