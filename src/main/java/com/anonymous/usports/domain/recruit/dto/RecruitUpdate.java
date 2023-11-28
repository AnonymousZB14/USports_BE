package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.type.Gender;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RecruitUpdate {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    private Long sportsId;
    private String title;
    private String content;
    private String placeName;
    private String lat;
    private String lnt;
    private int cost;
    private Gender gender;
    private int recruitCount;
    private LocalDateTime meetingDate;
    private int gradeFrom;
    private int gradeTo;

  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private Long recruitId;
    private String message;

    public Response(RecruitDto recruitDto){
      this.recruitId = recruitDto.getRecruitId();
      this.message = ResponseConstant.UPDATE_RECRUIT;
    }
  }

}
