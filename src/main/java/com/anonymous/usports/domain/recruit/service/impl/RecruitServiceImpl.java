package com.anonymous.usports.domain.recruit.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister.Request;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
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
  public RecruitDto addRecruit(Request request, Long memberId) {
    MemberEntity memberEntity = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    SportsEntity sportsEntity = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND));

    RecruitEntity saved =
        recruitRepository.save(Request.toEntity(request, memberEntity, sportsEntity));

    return RecruitDto.fromEntity(saved);
  }

  @Override
  @Transactional
  public RecruitDto getRecruit(Long recruitId) {
    return RecruitDto.fromEntity(
        recruitRepository.findById(recruitId)
            .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND))
    );
  }

  @Override
  @Transactional
  public RecruitDto updateRecruit(RecruitUpdate.Request request, Long recruitId, Long memberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));
    MemberEntity memberEntity = recruitEntity.getMember(); //작성자

    this.validateAuthority(memberEntity, memberId);

    SportsEntity sportsEntity = sportsRepository.findById(request.getSportsId())
        .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND));

    recruitEntity.updateRecruit(request, sportsEntity);

    RecruitEntity saved = recruitRepository.save(recruitEntity);

    return RecruitDto.fromEntity(saved);
  }

  @Override
  public RecruitDto deleteRecruit(Long recruitId, Long memberId) {
    RecruitEntity recruitEntity = recruitRepository.findById(recruitId)
        .orElseThrow(() -> new MyException(ErrorCode.RECRUIT_NOT_FOUND));
    MemberEntity memberEntity = recruitEntity.getMember(); //작성자

    this.validateAuthority(memberEntity, memberId);

    recruitRepository.delete(recruitEntity);

    return RecruitDto.fromEntity(recruitEntity);
  }

  private void validateAuthority(MemberEntity member, Long memberId){
    if(member.getMemberId() != memberId){
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}
