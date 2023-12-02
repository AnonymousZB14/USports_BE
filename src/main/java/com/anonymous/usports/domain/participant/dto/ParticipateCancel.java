package com.anonymous.usports.domain.participant.dto;


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
public class ParticipateCancel {
  private Long recruitId;
  private Long memberId;
  private String message;
}
