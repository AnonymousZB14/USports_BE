package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitStatus {

  RECRUITING("모집 중"),
  ALMOST_END("마감 임박"),
  END("마감");

  private final String description;
}
