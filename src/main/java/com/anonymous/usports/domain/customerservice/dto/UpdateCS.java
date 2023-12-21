package com.anonymous.usports.domain.customerservice.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UpdateCS {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request{
    @NotBlank(message="신고 제목은 필수 입력 사항입니다")
    private String title;

    @NotBlank(message="신고 내용은 필수 입력 사항입니다")
    private String content;
  }


  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response{
    private String title;
    private String content;
    private String message;

  }
}
