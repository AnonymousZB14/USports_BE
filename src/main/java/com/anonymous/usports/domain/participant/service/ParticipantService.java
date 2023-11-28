package com.anonymous.usports.domain.participant.service;

import com.anonymous.usports.domain.participant.dto.ParticipantDto;

public interface ParticipantService {

  /**
   * Recruit에 참여 신청 넣기
   */
  ParticipantDto joinRecruit(Long memberId, Long recruitId);

}
