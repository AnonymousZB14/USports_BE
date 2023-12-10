package com.anonymous.usports.domain.sportsskill.dto;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import com.anonymous.usports.global.type.SportsGrade;
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
public class SportsSkillDto {
  private Long sportsSkillId;
  private String sportsName;
  private SportsGrade sportsGrade;

  public SportsSkillDto(SportsSkillEntity sportsSkillEntity){
    this.sportsSkillId = sportsSkillEntity.getSports().getSportsId();
    this.sportsName = sportsSkillEntity.getSports().getSportsName();

    double score =
        (double) sportsSkillEntity.getSportsScore() / sportsSkillEntity.getEvaluateCount();
    this.sportsGrade = SportsGrade.doubleToGrade(score);
  }


}
