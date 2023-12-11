package com.anonymous.usports.domain.evaluation.service;

import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister;
import com.anonymous.usports.domain.member.entity.MemberEntity;

public interface EvaluationService {

  /**
   * 회원 간 평가 등록
   */
  EvaluationRegister.Response registerEvaluation(EvaluationRegister.Request request, Long loginMemberId);

}
