package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.type.SportsGrade;
import java.time.LocalDateTime;

/**
 * FE에서 사용하는 값으로 반환하는 RecruitResponse
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitResponse {
  private Long recruitId;

  private Long sportsId;

  private Long memberId;

  private String memberAccountName;

  private String title;

  private String content;

  private String placeName;

  private String region;

  private String streetNameAddr; // 도로명 주소

  private String streetNumberAddr; // 지번 주소

  private double lat;

  private double lnt;

  private int cost;

  private String gender;

  private int recruitCount;

  private LocalDateTime meetingDate;

  private String recruitStatus;

  private String gradeFrom;//필요

  private String gradeTo;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  private String participantSportsSkillAverage;

  public static RecruitResponse fromEntity(RecruitEntity recruit){
    return RecruitResponse.builder()
        .recruitId(recruit.getRecruitId())
        .sportsId(recruit.getSports().getSportsId())
        .memberId(recruit.getMember().getMemberId())
        .memberAccountName(recruit.getMember().getAccountName())
        .title(recruit.getTitle())
        .content(recruit.getContent())
        .placeName(recruit.getPlaceName())
        .region(recruit.getRegion())
        .streetNameAddr(recruit.getStreetNameAddr())
        .streetNumberAddr(recruit.getStreetNumberAddr())
        .lat(Double.parseDouble(recruit.getLat()))
        .lnt(Double.parseDouble(recruit.getLnt()))
        .cost(recruit.getCost())
        .gender(recruit.getGender().getDescription())
        .recruitCount(recruit.getRecruitCount())
        .meetingDate(recruit.getMeetingDate())
        .recruitStatus(recruit.getRecruitStatus().getDescription())
        .gradeFrom(SportsGrade.intToGrade(recruit.getGradeFrom()).getDescription())
        .gradeTo(SportsGrade.intToGrade(recruit.getGradeTo()).getDescription())
        .registeredAt(recruit.getRegisteredAt())
        .updatedAt(recruit.getUpdatedAt())
        .build();
  }
}
