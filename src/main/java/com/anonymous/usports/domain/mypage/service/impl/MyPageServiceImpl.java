package com.anonymous.usports.domain.mypage.service.impl;

import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.dto.MyPageMember;
import com.anonymous.usports.domain.mypage.service.MyPageService;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {

  private final MemberRepository memberRepository;
  private final InterestedSportsRepository interestedSportsRepository;
  private final SportsSkillRepository sportsSkillRepository;

  @Override
  public MyPageMainDto getMyPageMainData(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    //회원 정보
    MyPageMember myPageMember = this.getMyPageMember(member);
    //팝업으로 띄워줄 sportSkill
    List<SportsSkillDto> sportsSkills = this.getSportsSkills(member);
    //평가를 하기 위한 내가 참여했던 Recruit와 참여자 리스트 - RecruitAndParticipants
    //내 신청 현황
    //내가 만든 모집 관리
    //내 정보 수정

    return null;
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

  public List<SportsSkillDto> getSportsSkills(MemberEntity member) {
    return sportsSkillRepository.findAllByMember(member)
        .stream()
        .map(SportsSkillDto::new)
        .collect(Collectors.toList());
  }

}
