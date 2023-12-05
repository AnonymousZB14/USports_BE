package com.anonymous.usports.domain.evaluation.dto;

import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
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
public class MannerDto {

  private int kindness;
  private int passion;
  private int teamwork;

  public static MannerDto fromEvaluationEntity(EvaluationEntity evaluation){
    return MannerDto.builder()
        .kindness(evaluation.getKindness())
        .passion(evaluation.getPassion())
        .teamwork(evaluation.getTeamwork())
        .build();
  }

}
