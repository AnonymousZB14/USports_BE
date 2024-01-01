package com.anonymous.usports.domain.evaluation.service.impl;

import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Request;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Response;
import com.anonymous.usports.domain.evaluation.dto.MannerDto;
import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
import com.anonymous.usports.domain.evaluation.repository.EvaluationRepository;
import com.anonymous.usports.domain.evaluation.service.EvaluationService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.exception.EvaluationException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.ParticipantException;
import com.anonymous.usports.global.exception.RecruitException;
import java.time.LocalDateTime;
import java.util.Optional;
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
  private final SportsSkillRepository sportsSkillRepository;
  private final ParticipantRepository participantRepository;

  @Override
  public Response registerEvaluation(Request request, Long loginMemberId) {
    MemberEntity fromMember = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    MemberEntity toMember = memberRepository.findById(request.getToMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruit = recruitRepository.findById(request.getRecruitId())
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    SportsEntity sports = recruit.getSports();

    //두 member 모두 같은 recruit에 속해있는지 확인
    ParticipantEntity participantOfFromMember =
        participantRepository.findByMemberAndRecruit(fromMember, recruit)
            .orElseThrow(() -> new ParticipantException(ErrorCode.PARTICIPANT_NOT_FOUND));
    ParticipantEntity participantOfToMember =
        participantRepository.findByMemberAndRecruit(toMember, recruit)
            .orElseThrow(() -> new ParticipantException(ErrorCode.PARTICIPANT_NOT_FOUND));

    this.validateRecruitIsFinished(recruit);
    this.validateEvaluationAlreadyExists(recruit, fromMember, toMember);

    EvaluationEntity saved =
        evaluationRepository.save(Request.toEntity(request, recruit, fromMember, toMember));

    //MemberEntity에 업데이트
    toMember.updateManners(MannerDto.fromEvaluationEntity(saved));

    //SportsSkillEntity에 업데이트
    Optional<SportsSkillEntity> optionalSportsSkill = sportsSkillRepository.findByMemberAndSports(
        toMember, sports);
    if (optionalSportsSkill.isEmpty()) {
      sportsSkillRepository.save(new SportsSkillEntity(toMember, sports, request.getSportsScore()));
    } else {
      SportsSkillEntity sportsSkill = optionalSportsSkill.get();
      sportsSkill.updateSportsSkill(request.getSportsScore());
      sportsSkillRepository.save(sportsSkill);
    }

    //Participant에서 타인 평가 여부 변경
    participantOfFromMember.setEvaluationAt(LocalDateTime.now());
    participantRepository.save(participantOfFromMember);

    return EvaluationRegister.Response.fromEntity(saved);
  }

  private void validateRecruitIsFinished(RecruitEntity recruit) {
    if (recruit.getMeetingDate().isAfter(LocalDateTime.now())) {
      throw new EvaluationException(ErrorCode.RECRUIT_NOT_FINISHED);
    }
  }

  private void validateEvaluationAlreadyExists(RecruitEntity recruit, MemberEntity fromMember,
      MemberEntity toMember) {
    if (evaluationRepository
        .existsByRecruitAndFromMemberAndToMember(recruit, fromMember, toMember)) {
      throw new EvaluationException(ErrorCode.EVALUATION_ALREADY_EXISTS);
    }
  }

}
