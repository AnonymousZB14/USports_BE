package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.SportsGrade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class RecruitDto {

  private Long recruitId;

  private String sportsName;

  private Long memberId;

  private String title;

  private String content;

  private String placeName;

  private String region;

  private String streetNameAddr; // 도로명 주소

  private String streetNumberAddr; // 지번 주소

  private String lat;

  private String lnt;

  private int cost;

  private Gender gender;

  private int recruitCount;

  private LocalDateTime meetingDate;

  private String recruitStatus;

  private String gradeFrom;

  private String gradeTo;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  public static RecruitDto fromEntity(RecruitEntity recruitEntity){
    return RecruitDto.builder()
        .recruitId(recruitEntity.getRecruitId())
        .sportsName(recruitEntity.getSports().getSportsName())
        .memberId(recruitEntity.getMember().getMemberId())
        .title(recruitEntity.getTitle())
        .content(recruitEntity.getContent())
        .placeName(recruitEntity.getPlaceName())
        .region(recruitEntity.getRegion())
        .streetNameAddr(recruitEntity.getStreetNameAddr())
        .streetNumberAddr(recruitEntity.getStreetNumberAddr())
        .lat(recruitEntity.getLat())
        .lnt(recruitEntity.getLnt())
        .cost(recruitEntity.getCost())
        .gender(recruitEntity.getGender())
        .recruitCount(recruitEntity.getRecruitCount())
        .meetingDate(recruitEntity.getMeetingDate())
        .recruitStatus(recruitEntity.getRecruitStatus().getDescription())
        .gradeFrom(SportsGrade.intToString(recruitEntity.getGradeFrom()))
        .gradeTo(SportsGrade.intToString(recruitEntity.getGradeTo()))
        .registeredAt(recruitEntity.getRegisteredAt())
        .updatedAt(recruitEntity.getUpdatedAt())
        .build();
  }

}
