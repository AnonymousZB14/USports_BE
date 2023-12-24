package com.anonymous.usports.global.typeprovider.dto;

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
public class RecruitStatusDto {

  private RecruitStatus recruitStatus;
  private String description;

  public RecruitStatusDto(RecruitStatus recruitStatus) {
    this.recruitStatus = recruitStatus;
    this.description = recruitStatus.getDescription();
  }
}
