package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AddRecruit {

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
    private int recruitCount;
    private LocalDateTime meetingDate;
    private Gender gender;
    private int gradeFrom;
    private int gradeTo;

    public static RecruitEntity toEntity(Request request, MemberEntity member, SportsEntity sports){
      return RecruitEntity.builder()
          .sports(sports)
          .member(member)
          .title(request.getTitle())
          .content(request.getContent())
          .placeName(request.getPlaceName())
          .lat(request.getLat())
          .lnt(request.getLnt())
          .cost(request.getCost())
          .gender(request.getGender())
          .recruitCount(request.getRecruitCount())
          .meetingDate(request.getMeetingDate())
          .recruitStatus(RecruitStatus.RECRUITING)
          .gradeFrom(request.getGradeFrom())
          .gradeTo(request.getGradeTo())
          .registeredAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build();
    }

  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private Long recruitId;
    private String message;

    public static Response fromDto(RecruitDto recruitDto){
      return new Response(recruitDto.getRecruitId(), ResponseConstant.ADD_RECRUIT);
    }
  }

}
