package com.anonymous.usports.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {

  private Long id;
  private String message;

  public static FollowResponse Response(Long followId, String message) {
    return FollowResponse.builder()
        .id(followId)
        .message(message)
        .build();
  }
}
