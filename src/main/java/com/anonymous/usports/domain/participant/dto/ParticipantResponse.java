package com.anonymous.usports.domain.participant.dto;


import com.anonymous.usports.global.constant.ResponseConstant;

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
