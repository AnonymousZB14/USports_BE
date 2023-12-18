package com.anonymous.usports.domain.sports.dto;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SportsDto {

  private Long sportsId;
  private String sportsName;

  public SportsDto(SportsEntity sportsEntity) {
    this.sportsId = sportsEntity.getSportsId();
    this.sportsName = sportsEntity.getSportsName();
  }

}
