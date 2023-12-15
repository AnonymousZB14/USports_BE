package com.anonymous.usports.domain.participant.dto;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.SportsGrade;
import java.time.LocalDateTime;
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
public class ParticipantDto {

  private Long participantId;

  private Long memberId;

  private Long recruitId;

  private String sportsSkill;

  private LocalDateTime registeredAt;

  private LocalDateTime confirmedAt; //모집 확인 받은 시간

  private ParticipantStatus status;

  private LocalDateTime evaluationAt; //타인 평가 일시

  private LocalDateTime meetingDate; //Recruit의 모임 일시

  public static ParticipantDto fromEntity(ParticipantEntity participant){
    return ParticipantDto.builder()
        .participantId(participant.getParticipantId())
        .memberId(participant.getMember().getMemberId())
        .recruitId(participant.getRecruit().getRecruitId())
        .sportsSkill(SportsGrade.doubleToGrade(participant.getSportsSkill()).getDescription())
        .registeredAt(participant.getRegisteredAt())
        .confirmedAt(participant.getConfirmedAt())
        .status(participant.getStatus())
        .evaluationAt(participant.getEvaluationAt())
        .meetingDate(participant.getMeetingDate())
        .build();
  }
}
