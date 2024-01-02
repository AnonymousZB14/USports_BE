package com.anonymous.usports.domain.participant.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import com.anonymous.usports.domain.participant.dto.ParticipantInfo;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantManage.Response;
import com.anonymous.usports.domain.participant.dto.ParticipateCancel;
import com.anonymous.usports.domain.participant.dto.ParticipateResponse;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.constant.NotificationConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.ParticipantException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.RecruitStatus;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParticipantServiceImpl implements ParticipantService {

  private final MemberRepository memberRepository;
  private final RecruitRepository recruitRepository;
  private final ParticipantRepository participantRepository;
  private final SportsSkillRepository sportsSkillRepository;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public ParticipantListDto getParticipants(Long recruitId, Long loginMemberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    this.validateAuthority(recruitEntity, loginMemberId);

    List<ParticipantInfo> ingList =
        participantRepository
            .findAllByRecruitAndStatusOrderByParticipantId(recruitEntity, ParticipantStatus.ING)
            .stream()
            .map(ParticipantInfo::fromEntity)
            .collect(Collectors.toList());

    List<ParticipantInfo> acceptedList =
        participantRepository
            .findAllByRecruitAndStatusOrderByParticipantId(recruitEntity, ParticipantStatus.ACCEPTED)
            .stream()
            .map(ParticipantInfo::fromEntity)
            .collect(Collectors.toList());

    return ParticipantListDto.builder()
        .currentCount(recruitEntity.getCurrentCount())
        .totalCount(recruitEntity.getRecruitCount())
        .status(recruitEntity.getRecruitStatus())
        .ingList(ingList)
        .acceptedList(acceptedList)
        .build();
  }

  @Override
  @Transactional
  public ParticipateResponse joinRecruit(Long memberId, Long recruitId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    //신청 진행중
    Optional<ParticipantEntity> ingParticipant =
        participantRepository.findByMemberAndRecruitAndStatus(memberEntity, recruitEntity,
            ParticipantStatus.ING);
    if (ingParticipant.isPresent()) {
      return new ParticipateResponse(recruitId, memberId, ResponseConstant.JOIN_RECRUIT_ING);
    }
    //이미 수락된 상태
    Optional<ParticipantEntity> acceptedParticipant =
        participantRepository.findByMemberAndRecruitAndStatus(memberEntity, recruitEntity,
            ParticipantStatus.ACCEPTED);
    if (acceptedParticipant.isPresent()) {
      return new ParticipateResponse(recruitId, memberId,
          ResponseConstant.JOIN_RECRUIT_ALREADY_ACCEPTED);
    }

    //participant에 SportsSkill 추가하기
    ParticipantEntity participant = new ParticipantEntity(memberEntity, recruitEntity);
    Optional<SportsSkillEntity> optionalSportsSkill =
        sportsSkillRepository.findByMemberAndSports(memberEntity, recruitEntity.getSports());

    if (optionalSportsSkill.isPresent()) {
      SportsSkillEntity sportsSkill = optionalSportsSkill.get();
      participant.setSportsSkill(sportsSkill.getSportsScoreAsDouble());
    } else {
      participant.setSportsSkill(0.0);
    }

    //신청 가능 -> 신청
    ParticipantEntity saved = participantRepository.save(participant);

    notificationService.notify(recruitEntity.getMember(), new NotificationCreateDto(saved, recruitEntity));

    return new ParticipateResponse(recruitId, memberId, ResponseConstant.JOIN_RECRUIT_COMPLETE);
  }

  @Override
  @Transactional
  public Response manageJoinRecruit(ParticipantManage.Request request, Long recruitId,
      Long loginMemberId) {
    MemberEntity applicant = memberRepository.findById(request.getApplicantId())
        .orElseThrow(() -> new MemberException(ErrorCode.APPLICANT_MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    this.validateAuthority(recruitEntity, loginMemberId);
    if (recruitEntity.getRecruitStatus() == RecruitStatus.END) {
      throw new RecruitException(ErrorCode.RECRUIT_ALREADY_END);
    }

    ParticipantEntity participantEntity =
        participantRepository.findByMemberAndRecruitAndStatus(applicant, recruitEntity,
                ParticipantStatus.ING)
            .orElseThrow(() -> new ParticipantException(ErrorCode.PARTICIPANT_NOT_FOUND));

    //거절
    if (!request.isAccept()) {
      participantEntity.setStatus(ParticipantStatus.REFUSED);
      participantRepository.save(participantEntity);
      return new ParticipantManage.Response(recruitId, applicant.getMemberId(), false);
    }

    //수락 시
    //참여 수락 상태로 변경
    participantEntity.setStatus(ParticipantStatus.ACCEPTED);
    participantRepository.save(participantEntity);

    recruitEntity.participantAdded();//Recruit의 currentCount + 1

    recruitRepository.save(recruitEntity);

    return new ParticipantManage.Response(recruitId, applicant.getMemberId(), true);
  }

  private void validateAuthority(RecruitEntity recruit, Long loginMemberId) {
    if (!Objects.equals(recruit.getMember().getMemberId(), loginMemberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }

  @Override
  public ParticipateCancel cancelJoinRecruit(Long recruitId, Long loginMemberId) {
    MemberEntity applicant = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.APPLICANT_MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));

    //ING 상태의 참여 신청 찾기
    Optional<ParticipantEntity> ing = participantRepository.findByMemberAndRecruitAndStatus(
        applicant, recruitEntity, ParticipantStatus.ING);
    if (ing.isPresent()) {
      participantRepository.delete(ing.get());
      return new ParticipateCancel(recruitId, loginMemberId, ResponseConstant.CANCEL_JOIN_RECRUIT);
    }
    //ACCEPTED 상태의 참여 신청 찾기
    Optional<ParticipantEntity> accepted = participantRepository.findByMemberAndRecruitAndStatus(
        applicant, recruitEntity, ParticipantStatus.ACCEPTED);
    if (accepted.isPresent()) {
      participantRepository.delete(accepted.get());
      recruitEntity.participantCanceled();
      return new ParticipateCancel(recruitId, loginMemberId, ResponseConstant.CANCEL_JOIN_RECRUIT);
    }

    //아무것도 찾지 못한 경우
    throw new ParticipantException(ErrorCode.PARTICIPANT_NOT_FOUND);
  }
}
