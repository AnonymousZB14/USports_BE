package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.global.type.ParticipantStatus;

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
public class MyPageParticipant {

  private String sportsName;
  private String recruitTile;
  private ParticipantStatus status;

}
