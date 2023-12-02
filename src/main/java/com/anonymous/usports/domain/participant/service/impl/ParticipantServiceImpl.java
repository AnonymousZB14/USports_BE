package com.anonymous.usports.domain.participant.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
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
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.RecruitStatus;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParticipantServiceImpl implements ParticipantService {

  private final MemberRepository memberRepository;
  private final RecruitRepository recruitRepository;
  private final ParticipantRepository participantRepository;

  @Override
  @Transactional
  public ParticipantListDto getParticipants(Long recruitId, int page, Long loginMemberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

    this.validateAuthority(recruitEntity, loginMemberId);

    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
    Page<ParticipantEntity> findPage =
        participantRepository.findAllByRecruitAndStatusOrderByParticipantId(
            recruitEntity, ParticipantStatus.ING, pageRequest);

    return ParticipantListDto.fromEntityPage(findPage);
  }

  @Override
  @Transactional
  public ParticipateResponse joinRecruit(Long memberId, Long recruitId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

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

    //신청 가능 -> 신청
    participantRepository.save(new ParticipantEntity(memberEntity, recruitEntity));

    return new ParticipateResponse(recruitId, memberId, ResponseConstant.JOIN_RECRUIT_COMPLETE);
  }

  @Override
  @Transactional
  public Response manageJoinRecruit(ParticipantManage.Request request, Long recruitId,
      Long loginMemberId) {
    MemberEntity applicant = memberRepository.findById(request.getApplicantId())
        .orElseThrow(() -> new MyException(ErrorCode.APPLICANT_MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

    this.validateAuthority(recruitEntity, loginMemberId);
    if (recruitEntity.getRecruitStatus() == RecruitStatus.END) {
      throw new MyException(ErrorCode.RECRUIT_ALREADY_END);
    }

    ParticipantEntity participantEntity =
        participantRepository.findByMemberAndRecruitAndStatus(applicant, recruitEntity,
                ParticipantStatus.ING)
            .orElseThrow(() -> new MyException(ErrorCode.PARTICIPANT_NOT_FOUND));

    //거절
    if (!request.isAccept()) {
      participantEntity.refuse();
      participantRepository.save(participantEntity);
      return new ParticipantManage.Response(recruitId, applicant.getMemberId(), false);
    }

    //수락 시
    //참여 수락 상태로 변경
    participantEntity.confirm();
    participantRepository.save(participantEntity);

    recruitEntity.participantAdded();//Recruit의 currentCount + 1

    //수락 후 마감됨
    if (recruitEntity.getCurrentCount() == recruitEntity.getRecruitCount()) {
      recruitEntity.statusToEnd();
    }
    recruitRepository.save(recruitEntity);

    return new ParticipantManage.Response(recruitId, applicant.getMemberId(), true);
  }

  private void validateAuthority(RecruitEntity recruit, Long loginMemberId) {
    if (!Objects.equals(recruit.getMember().getMemberId(), loginMemberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }

  @Override
  public ParticipateCancel cancelJoinRecruit(Long recruitId, Long memberId) {
    MemberEntity applicant = memberRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.APPLICANT_MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

    //ING 상태의 참여 신청 찾기
    Optional<ParticipantEntity> ing = participantRepository.findByMemberAndRecruitAndStatus(
        applicant, recruitEntity, ParticipantStatus.ING);
    if (ing.isPresent()) {
      participantRepository.delete(ing.get());
      return new ParticipateCancel(recruitId, memberId, ResponseConstant.CANCEL_JOIN_RECRUIT);
    }
    //ACCEPTED 상태의 참여 신청 찾기
    Optional<ParticipantEntity> accepted = participantRepository.findByMemberAndRecruitAndStatus(
        applicant, recruitEntity, ParticipantStatus.ACCEPTED);
    if (accepted.isPresent()) {
      participantRepository.delete(accepted.get());
      return new ParticipateCancel(recruitId, memberId, ResponseConstant.CANCEL_JOIN_RECRUIT);
    }

    //아무것도 찾지 못한 경우
    throw new MyException(ErrorCode.PARTICIPANT_NOT_FOUND);
  }
}
