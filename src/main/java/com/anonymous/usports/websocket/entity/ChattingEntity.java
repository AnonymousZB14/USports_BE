package com.anonymous.usports.websocket.entity;

import com.anonymous.usports.websocket.type.MessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document(collection = "chatting")
public class ChattingEntity {

  @Id//Id 자동 생성
  private String id;

  @Indexed//해당 컬럼으로 인덱싱 -> 이 컬럼으로 검색 속도 빨라짐
  private Long chatRoomId;

  private Long memberId; //회원 Id

  private String content; //내용

  private LocalDateTime createdAt; //입력 시간

  private MessageType type; //메시지 타입
}
