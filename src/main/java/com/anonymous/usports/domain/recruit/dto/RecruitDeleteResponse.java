package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.global.constant.ResponseConstant;

public class RecruitDeleteResponse {

  private Long recruitId;
  private String message;

  public RecruitDeleteResponse(RecruitDto recruitDto){
    this.recruitId = recruitDto.getRecruitId();
    this.message = ResponseConstant.DELETE_RECRUIT;
  }

}
