package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CsStatus {

  Registered("문의가 등록이 되었습니다"),
  ING("문의를 해결하는 중입니다"),
  Finished("해결이 되었습니다");

  private final String description;
}
