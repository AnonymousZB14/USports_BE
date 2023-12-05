package com.anonymous.usports.domain.evaluation.service.impl;

import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Request;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Response;
import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
import com.anonymous.usports.domain.evaluation.repository.EvaluationRepository;
import com.anonymous.usports.domain.evaluation.service.EvaluationService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.global.EvaluationException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EvaluationServiceImpl implements EvaluationService {

  private final MemberRepository memberRepository;
  private final RecruitRepository recruitRepository;
  private final EvaluationRepository evaluationRepository;

  @Override
  public Response registerEvaluation(Request request, Long loginMemberId) {
    MemberEntity fromMember = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    MemberEntity toMember = memberRepository.findById(request.getToMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruit = recruitRepository.findById(request.getRecruitId())
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    //모임이 종료된 Recruit인지 확인
    if(recruit.getRecruitStatus() == RecruitStatus.RECRUITING){
      throw new EvaluationException(ErrorCode.RECRUIT_NOT_FINISHED);
    }

    //이미 해당 모임에 대한 fromMember -> toMember의 평가가 존재하는지 확인
    if(evaluationRepository
        .existsByRecruitAndFromMemberAndToMember(recruit, fromMember, toMember)){
      throw new EvaluationException(ErrorCode.EVALUATION_ALREADY_EXISTS);
    }

    EvaluationEntity saved =
        evaluationRepository.save(Request.toEntity(request, recruit, fromMember, toMember));


    return EvaluationRegister.Response.fromEntity(saved);
  }
}
