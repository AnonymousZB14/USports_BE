package com.anonymous.usports.domain.participant.service;

import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;

public interface ParticipantService {

  /**
   * Recruit에 대한 참여 신청자 리스트 조회
   */
  ParticipantListDto getParticipants(Long recruitId, int page, Long memberId);


  /**
   * Recruit에 참여 신청 넣기
   */
  ParticipantDto joinRecruit(Long memberId, Long recruitId);

  /**
   * 참여 신청 수락 or 거절
   */
  ParticipantManage.Response manageJoinRecruit(ParticipantManage.Request request, Long recruitId, Long loginMemberId);
}
