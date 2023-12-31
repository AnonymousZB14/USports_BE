package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
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
  private Long recruitId;
  private String recruitTile;
  private ParticipantStatus status;

  public MyPageParticipant(ParticipantEntity participant) {
    this.sportsName = participant.getRecruit().getSports().getSportsName();
    this.recruitId = participant.getRecruit().getRecruitId();
    this.recruitTile = participant.getRecruit().getTitle();
    this.status = participant.getStatus();
  }
}
