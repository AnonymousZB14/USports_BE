package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CsStatus {

  REGISTERED(1, "문의가 등록이 되었습니다"),
  ING(2, "문의를 해결하는 중입니다"),
  FINISHED(3, "해결이 되었습니다");

  private final int value;
  private final String description;
}
