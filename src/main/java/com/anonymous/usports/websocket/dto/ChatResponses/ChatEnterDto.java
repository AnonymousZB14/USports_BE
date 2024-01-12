package com.anonymous.usports.websocket.dto.ChatResponses;

import com.anonymous.usports.domain.member.dto.MemberDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatEnterDto {

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {
    private Long chatRoomId;
    private String chatRoomName;
    private Long recruitId;
    private List<MemberDto> members;
  }

}
