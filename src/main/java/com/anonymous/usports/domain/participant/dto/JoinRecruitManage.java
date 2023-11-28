package com.anonymous.usports.domain.participant.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class JoinRecruitManage {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request{
    private boolean accept;
    private Long applicantId;
  }

  public static class Response{
    private Long recruitId;
    private Long applicantId;
    private String message;

    public Response(Long recruitId, Long applicantId, boolean result){
      this.recruitId = recruitId;
      this.applicantId = applicantId;
      if(result){
        this.message = ResponseConstant.JOIN_RECRUIT_ACCEPTED;
      }else{
        this.message = ResponseConstant.JOIN_RECRUIT_REJECTED;
      }
    }

  }

}
