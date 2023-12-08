package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
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
public class MyPageRecruit {

  private Long recruitId;

  private String sportsName;

  private String title;

  private Gender gender;

  private RecruitStatus status;

  public MyPageRecruit(RecruitEntity recruit) {
    this.recruitId =  recruit.getRecruitId();
    this.sportsName = recruit.getSports().getSportsName();
    this.title = recruit.getTitle();
    this.gender =recruit.getGender();
    this.status =  recruit.getRecruitStatus();
  }
}
