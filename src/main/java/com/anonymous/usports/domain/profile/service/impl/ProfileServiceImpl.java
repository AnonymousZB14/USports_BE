package com.anonymous.usports.domain.profile.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.mypage.service.MyPageService;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.profile.dto.ProfileMember;
import com.anonymous.usports.domain.profile.service.ProfileService;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.ParticipantStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

  private final MyPageService myPageService;
  private final MemberRepository memberRepository;
  private final RecordRepository recordRepository;
  private final ParticipantRepository participantRepository;

  @Override
  public ProfileMember profileMember(String accountName) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    return ProfileMember.builder()
        .memberProfile(myPageService.getMyPageMember(member))
        .sportsSkills(myPageService.getSportsSkills(member))
        .build();
  }

  @Override
  public RecordListDto profileRecords(String accountName, Integer page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Page<RecordEntity> recordEntityPage = recordRepository
        .findByMemberOrderByRegisteredAtDesc(
            member, PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_PROFILE));

    return new RecordListDto(recordEntityPage);
  }

  @Override
  public RecruitListDto profileRecruits(String accountName, Integer page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    Page<ParticipantEntity> participantEntityPage =
        participantRepository.findByMemberAndStatusAndMeetingDateBefore(
            member,
            ParticipantStatus.ACCEPTED,
            LocalDateTime.now(),
            PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT,
                Sort.by("meetingDate").descending()));

    List<RecruitEntity> recruitEntityList =
        participantEntityPage.getContent().stream()
            .map(ParticipantEntity::getRecruit)
            .collect(Collectors.toList());

    return new RecruitListDto(participantEntityPage, recruitEntityList);
  }
}
