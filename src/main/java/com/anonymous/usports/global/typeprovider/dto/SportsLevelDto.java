package com.anonymous.usports.global.typeprovider.dto;

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
public class SportsLevelDto {
  private SportsGrade sportsGrade;
  private String description;

  public SportsLevelDto(SportsGrade sportsGrade) {
    this.sportsGrade = sportsGrade;
    this.description = sportsGrade.getDescription();
  }
}
