package com.anonymous.usports.domain.participant.dto;


import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
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
public class ParticipantInfo {

  private Long memberId;
  private String accountName;
  private String gender;
  private String sportsSkill;
  private String status;

  public static ParticipantInfo fromEntity(ParticipantEntity participant){
    return ParticipantInfo.builder()
        .memberId(participant.getMember().getMemberId())
        .accountName(participant.getMember().getAccountName())
        .gender(participant.getMember().getGender().getDescription())
        .sportsSkill(SportsGrade.doubleToGrade(participant.getSportsSkill()).getDescription())
        .status(participant.getStatus().getDescription())
        .build();
  }
}
