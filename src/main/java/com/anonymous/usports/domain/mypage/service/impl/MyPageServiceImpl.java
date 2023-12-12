package com.anonymous.usports.domain.mypage.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.dto.MyPageMember;
import com.anonymous.usports.domain.mypage.dto.MyPageParticipant;
import com.anonymous.usports.domain.mypage.dto.MyPageRecruit;
import com.anonymous.usports.domain.mypage.dto.RecruitAndParticipants;
import com.anonymous.usports.domain.mypage.service.MyPageService;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.ParticipantStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {

  private final MemberRepository memberRepository;
  private final InterestedSportsRepository interestedSportsRepository;
  private final SportsSkillRepository sportsSkillRepository;
  private final RecruitRepository recruitRepository;
  private final ParticipantRepository participantRepository;

  @Override
  @Transactional
  public MyPageMainDto getMyPageMainData(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    //회원 정보
    MyPageMember myPageMember = this.getMyPageMember(member);

    //팝업으로 띄워줄 sportSkill
    List<SportsSkillDto> sportsSkills = this.getSportsSkills(member);

    //평가하기 : 평가를 하기 위한 내가 참여했던 Recruit와 참여자 리스트
    List<RecruitAndParticipants> recruitAndParticipants = this.getRecruitAndParticipants(member);

    //내 신청 현황 : 내가 신청한 참여신청(Participant) 리스트
    List<MyPageParticipant> participantList = this.getParticipantList(member);

    //내 모집 관리 : 내가 만든 모집 관리
    List<MyPageRecruit> myRecruitList = this.getMyRecruitList(member);


    return MyPageMainDto.builder()
        .memberProfile(myPageMember)
        .sportsSkills(sportsSkills)
        .recruitAndParticipants(recruitAndParticipants)
        .participateList(participantList)
        .myRecruitList(myRecruitList)
        .build();
  }

  public MyPageMember getMyPageMember(MemberEntity member) {
    List<InterestedSportsEntity> interestedSportsEntityList =
        interestedSportsRepository.findAllByMemberEntity(member);
    int listSize = interestedSportsEntityList.size();

    List<String> interestSportsList = new ArrayList<>();

    if (listSize == 0) {
      interestSportsList.add("none");
      return new MyPageMember(member, interestSportsList, 0);
    }

    int plusAlpha = 0;
    if (listSize > 3) {
      plusAlpha = listSize - 3;
      Random random = new Random();
      for (int i = 0; i < 3; i++) {
        interestSportsList.add(
            interestedSportsEntityList
                .get(random.nextInt(listSize))
                .getSports()
                .getSportsName());
      }

    } else {
      for (InterestedSportsEntity interestedSports : interestedSportsEntityList) {
        interestSportsList.add(interestedSports.getSports().getSportsName());
      }
    }

    return new MyPageMember(member, interestSportsList, plusAlpha);
  }

  /**
   * 팝업으로 띄워줄 sportSkill
   */
  public List<SportsSkillDto> getSportsSkills(MemberEntity member) {
    return sportsSkillRepository.findAllByMember(member)
        .stream()
        .map(SportsSkillDto::new)
        .collect(Collectors.toList());
  }

  /**
   * 평가하기 : 평가를 하기 위한 내가 참여했던 Recruit와 참여자 리스트
   */
  public List<RecruitAndParticipants> getRecruitAndParticipants(MemberEntity member) {
    //끝난지 48시간 이내의 참여 신청 건
    List<ParticipantEntity> thisMemberParticipateList = participantRepository
        .findAllByMemberAndMeetingDateIsAfter(member, LocalDateTime.now().minusDays(2L));
    if (thisMemberParticipateList.isEmpty()) {
      return new ArrayList<>();
    }

    List<RecruitAndParticipants> recruitAndParticipants = new ArrayList<>();

    for (ParticipantEntity loginMemberParticipate : thisMemberParticipateList) {
      RecruitEntity recruit = loginMemberParticipate.getRecruit();
      if(recruit.getMeetingDate().isAfter(LocalDateTime.now())){
        continue;
      }

      List<ParticipantEntity> otherParticipants =
          participantRepository.findAllByRecruitAndStatus(recruit, ParticipantStatus.ACCEPTED);
      List<MemberDto> memberList = new ArrayList<>();

      for (ParticipantEntity otherParticipant : otherParticipants) {
        MemberEntity participantMember = otherParticipant.getMember();
        if (Objects.equals(participantMember, member)) {
          continue;
        }
        memberList.add(MemberDto.fromEntity(participantMember));
      }
      recruitAndParticipants
          .add(new RecruitAndParticipants(RecruitDto.fromEntity(recruit), memberList));

    }
    return recruitAndParticipants;
  }

  /**
   * 내 신청 현황 : 내가 신청한 참여신청(Participant) 리스트
   */
  public List<MyPageParticipant> getParticipantList(MemberEntity member) {
    List<MyPageParticipant> list = new ArrayList<>();

    for (ParticipantEntity participant :
        participantRepository.findAllByMemberAndMeetingDateIsAfter(member, LocalDateTime.now())) {

      list.add(new MyPageParticipant(participant));
    }
    return list;
  }

  /**
   * 내 모집 관리 : 내가 만든 모집 관리
   */
  public List<MyPageRecruit> getMyRecruitList(MemberEntity member) {
    List<RecruitEntity> findList =
        recruitRepository.findAllByMemberAndMeetingDateIsAfter(member, LocalDateTime.now().minusHours(6));
    List<MyPageRecruit> list = new ArrayList<>();
    for (RecruitEntity recruit : findList) {
      list.add(new MyPageRecruit(recruit));
    }
    return list;
  }


}
