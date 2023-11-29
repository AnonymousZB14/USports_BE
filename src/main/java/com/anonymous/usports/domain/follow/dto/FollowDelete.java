package com.anonymous.usports.domain.follow.dto;

import com.anonymous.usports.domain.follow.dto.FollowRegister.Response;
import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FollowDelete {

  @Getter
  @Setter
  public static class Response extends FollowResponse{
    public Response() {
      super(ResponseConstant.DELETE_FOLLOW);
    }
  }

}
