package com.anonymous.usports.domain.participant.dto;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
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

  private LocalDateTime registeredAt;

  private LocalDateTime confirmedAt; //모집 수락 받은 시간

  private LocalDateTime evaluationAt; //타인 평가 일시

  public static ParticipantDto fromEntity(ParticipantEntity participant){
    return ParticipantDto.builder()
        .participantId(participant.getParticipantId())
        .memberId(participant.getMember().getMemberId())
        .recruitId(participant.getRecruit().getRecruitId())
        .registeredAt(participant.getRegisteredAt())
        .confirmedAt(participant.getConfirmedAt())
        .evaluationAt(participant.getEvaluationAt())
        .build();
  }
}
