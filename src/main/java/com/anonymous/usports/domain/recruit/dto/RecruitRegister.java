package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.api.dto.AddressDto;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.SportsGrade;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

public class RecruitRegister {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {
    private Long sportsId;
    private String title;
    private String content;

    private String region;

    private int cost;
    private int recruitCount;

    private String gender;

    private String gradeFrom;
    private String gradeTo;

    private String address;
    private String postCode;
    private String placeName;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime meetingDate;

    public static RecruitEntity toEntity(Request request, MemberEntity member, SportsEntity sports, AddressDto addressDto){
      return RecruitEntity.builder()
          .sports(sports)
          .member(member)
          .title(request.getTitle())
          .content(request.getContent())
          .placeName(request.getPlaceName())
          .region(request.getRegion())
          .streetNameAddr(addressDto.getRoadNameAddress())
          .streetNumberAddr(addressDto.getRoadNumberAddress())
          .postCode(request.postCode)
          .lat(addressDto.getLat())
          .lnt(addressDto.getLnt())
          .cost(request.getCost())
          .gender(Gender.of(request.getGender()))
          .currentCount(1)
          .recruitCount(request.getRecruitCount())
          .meetingDate(request.getMeetingDate())
          .recruitStatus(RecruitStatus.RECRUITING)
          .gradeFrom(SportsGrade.stringToInt(request.getGradeFrom()))
          .gradeTo(SportsGrade.stringToInt(request.getGradeTo()))
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

    public Response(RecruitDto recruitDto){
      this.recruitId = recruitDto.getRecruitId();
      this.message = ResponseConstant.REGISTER_RECRUIT;
    }
  }

}
