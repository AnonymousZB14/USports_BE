package com.anonymous.usports.domain.follow.dto;

import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate.Response;
import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FollowRegister {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    private Long fromMemberId;
    private Long toMemberId;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response extends FollowResponse{
    private Long followId;

    public Response(FollowDto followDto) {
      super(ResponseConstant.REGISTER_FOLLOW);
      this.followId = followDto.getFollowId();
    }
  }

}