package com.anonymous.usports.domain.participant.dto;


import com.anonymous.usports.global.constant.ResponseConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateResponse {
  private Long recruitId;
  private Long memberId;
  private String message;
}
