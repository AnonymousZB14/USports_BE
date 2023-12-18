package com.anonymous.usports.domain.customerservice.dto;

import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RegisterCS {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request{
    private String title;
    private String content;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response{
    private Long csId;
    private String title;
    private String content;
    private String status;

    public static Response fromEntity(CsEntity cs) {
      return Response.builder()
          .csId(cs.getCsId())
          .title(cs.getTitle())
          .content(cs.getContent())
          .status(cs.getCsStatus().getDescription())
          .build();
    }
  }
}
