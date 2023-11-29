package com.anonymous.usports.domain.participant.dto;


import com.anonymous.usports.global.constant.ResponseConstant;

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
public class ParticipantResponse {
  private Long recruitId;
  private Long memberId;
  private String message;

  public ParticipantResponse(ParticipantDto participantDto) {
    this.recruitId = participantDto.getRecruitId();
    this.memberId = participantDto.getMemberId();
    this.message = ResponseConstant.JOIN_RECRUIT;
  }
}
