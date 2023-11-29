package com.anonymous.usports.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FollowResponse {
  private String message;

  public FollowResponse(String message) {
    this.message = message;
  }

}
