package com.anonymous.usports.domain.participant.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantManage.Response;
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
        participantRepository.findAllByRecruitOrderByParticipantId(recruitEntity, pageRequest);

    return ParticipantListDto.fromEntityPage(findPage);
  }

  @Override
  @Transactional
  public ParticipateResponse joinRecruit(Long memberId, Long recruitId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

    Optional<ParticipantEntity> optionalParticipant =
        participantRepository.findByMemberAndRecruit(memberEntity, recruitEntity);

    if (optionalParticipant.isPresent()) {
      ParticipantEntity participantEntity = optionalParticipant.get();
      //신청 진행 중
      if (Objects.isNull(participantEntity.getConfirmedAt())) {
        return new ParticipateResponse(recruitId, memberId, ResponseConstant.JOIN_RECRUIT_ING);
      }
      //이미 수락 된 상태
      return new ParticipateResponse(
          recruitId, memberId, ResponseConstant.JOIN_RECRUIT_ALREADY_CONFIRMED);
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
    if(recruitEntity.getRecruitStatus() == RecruitStatus.END){
      throw new MyException(ErrorCode.RECRUIT_ALREADY_END);
    }

    ParticipantEntity participantEntity =
        participantRepository.findByMemberAndRecruit(applicant, recruitEntity)
            .orElseThrow(() -> new MyException(ErrorCode.PARTICIPANT_NOT_FOUND));

    if (!request.isAccept()) {
      //거절 시
      participantRepository.delete(participantEntity);
    } else {
      //수락 시
      //참여 수락 상태로 변경
      participantEntity.confirm();
      participantRepository.save(participantEntity);
      //Recruit의 currentCount + 1
      recruitEntity.participantAdded();
      recruitRepository.save(recruitEntity);
    }

    return new ParticipantManage.Response(recruitId, applicant.getMemberId(), request.isAccept());
  }

  private void validateAuthority(RecruitEntity recruit, Long loginMemberId) {
    if (!Objects.equals(recruit.getMember().getMemberId(), loginMemberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}
