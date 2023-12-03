package com.anonymous.usports.domain.recruit.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitEndResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister.Request;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.exception.SportsException;
import com.anonymous.usports.global.type.RecruitStatus;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecruitServiceImpl implements RecruitService {

  private final MemberRepository memberRepository;
  private final SportsRepository sportsRepository;
  private final RecruitRepository recruitRepository;

  @Override
  @Transactional
  public RecruitDto registerRecruit(Request request, Long memberId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    SportsEntity sportsEntity = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new SportsException(ErrorCode.SPORTS_NOT_FOUND));

    RecruitEntity saved =
        recruitRepository.save(Request.toEntity(request, memberEntity, sportsEntity));

    return RecruitDto.fromEntity(saved);
  }

  @Override
  @Transactional
  public RecruitDto getRecruit(Long recruitId) {
    return RecruitDto.fromEntity(
        recruitRepository.findById(recruitId)
            .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND))
    );
  }

  @Override
  @Transactional
  public RecruitDto updateRecruit(RecruitUpdate.Request request, Long recruitId, Long memberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));
    MemberEntity memberEntity = recruitEntity.getMember(); //작성자

    this.validateAuthority(memberEntity, memberId);

    SportsEntity sportsEntity = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new SportsException(ErrorCode.SPORTS_NOT_FOUND));

    recruitEntity.updateRecruit(request, sportsEntity);

    RecruitEntity saved = recruitRepository.save(recruitEntity);

    return RecruitDto.fromEntity(saved);
  }

  @Override
  @Transactional
  public RecruitDto deleteRecruit(Long recruitId, Long memberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));
    MemberEntity memberEntity = recruitEntity.getMember(); //작성자

    this.validateAuthority(memberEntity, memberId);

    recruitRepository.delete(recruitEntity);

    return RecruitDto.fromEntity(recruitEntity);
  }

  @Override
  @Transactional
  public RecruitEndResponse endRecruit(Long recruitId, Long loginMemberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new RecruitException(ErrorCode.RECRUIT_NOT_FOUND));
    MemberEntity memberEntity = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    this.validateAuthority(memberEntity, loginMemberId);

    //인원이 가득 찬 상태 -> 모집 마감 취소 불가
    if (recruitEntity.getCurrentCount() == recruitEntity.getRecruitCount()) {
      return new RecruitEndResponse(recruitId, ResponseConstant.END_RECRUIT_CANCEL_REFUSED);
    }

    //END -> 모집 중 상태로 변경 모집 인원수에 따라 RECRUITING 또는 ALMOST_FINISHED로 수정
    if (recruitEntity.getRecruitStatus() == RecruitStatus.END) {
      double ratio = (double) recruitEntity.getCurrentCount() / recruitEntity.getRecruitCount();

      //인원이 70% 이상 차서, ALMOST_FINISHED 상태로 변경
      if (ratio >= 0.7) {
        recruitEntity.statusToAlmostFinished();
      } else { //RECRUITING으로 변경
        recruitEntity.statusToRecruiting();
      }
      return new RecruitEndResponse(recruitId, ResponseConstant.END_RECRUIT_CANCELED);
    }

    //RECRUITING or ALMOST_FINISHED-> 모집 마감 상태로 변경
    recruitEntity.statusToEnd();
    return new RecruitEndResponse(recruitId, ResponseConstant.END_RECRUIT_COMPLETE);

  }

  @Override
  @Transactional
  public void getRecruitsByConditions(int page, String search, String place, String sports,
      String gender, String close) {

  }

  private void validateAuthority(MemberEntity member, Long memberId) {
    if (!Objects.equals(member.getMemberId(), memberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}
