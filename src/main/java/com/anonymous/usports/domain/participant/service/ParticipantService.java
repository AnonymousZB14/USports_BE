package com.anonymous.usports.domain.participant.service;

import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipateCancel;
import com.anonymous.usports.domain.participant.dto.ParticipateResponse;

public interface ParticipantService {

  /**
   * Recruit에 대한 참여 신청 진행중인 리스트 조회
   */
  ParticipantListDto getParticipants(Long recruitId, Long memberId);


  /**
   * Recruit에 참여 신청 넣기
   */
  ParticipateResponse joinRecruit(Long memberId, Long recruitId);

  /**
   * 참여 신청 수락 or 거절
   */
  ParticipantManage.Response manageJoinRecruit(ParticipantManage.Request request, Long recruitId,
      Long loginMemberId);

  /**
   * 참여 신청 취소
   */
  ParticipateCancel cancelJoinRecruit(Long recruitId, Long memberId);
}
