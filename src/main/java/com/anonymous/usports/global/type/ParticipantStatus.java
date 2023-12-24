package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantStatus {
  ACCEPTED("수락"),
  REFUSED("거절"),
  ING("신청중");

  private final String description;
}
