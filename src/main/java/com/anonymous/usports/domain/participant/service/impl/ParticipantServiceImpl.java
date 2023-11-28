package com.anonymous.usports.domain.participant.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantManage.Response;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        participantRepository.findAllByRecruitIdOOrderByParticipantId(recruitId, pageRequest);

    return ParticipantListDto.fromEntityPage(findPage);
  }

  @Override
  @Transactional
  public ParticipantDto joinRecruit(Long memberId, Long recruitId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));

    ParticipantEntity saved =
        participantRepository.save(new ParticipantEntity(memberEntity, recruitEntity));

    return ParticipantDto.fromEntity(saved);
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

    ParticipantEntity participantEntity =
        participantRepository.findByMemberIdAndRecruitId(request.getApplicantId(), recruitId)
            .orElseThrow(() -> new MyException(ErrorCode.PARTICIPANT_NOT_FOUND));

    if (!request.isAccept()) {
      //거절 시
      participantRepository.delete(participantEntity);
    } else {
      //수락 시
      participantEntity.confirm();
      participantRepository.save(participantEntity);
    }

    return new ParticipantManage.Response(recruitId, applicant.getMemberId(), request.isAccept());
  }

  private void validateAuthority(RecruitEntity recruit, Long loginMemberId){
    if (!Objects.equals(recruit.getMember().getMemberId(), loginMemberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}
