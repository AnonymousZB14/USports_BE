package com.anonymous.usports.domain.mypage.service;

import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.dto.MemberInfo;
import com.anonymous.usports.domain.mypage.dto.MyPageParticipant;
import com.anonymous.usports.domain.mypage.dto.MyPageRecruit;
import com.anonymous.usports.domain.mypage.dto.RecruitAndParticipants;
import com.anonymous.usports.domain.sports.dto.SportsDto;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import java.util.List;

public interface MyPageService {

  MyPageMainDto getMyPageMainData(Long memberId);

  MemberInfo getMemberInfo(Long memberId);

  List<SportsDto> getInterestedSportsList(Long memberId);

  List<SportsSkillDto> getSportsSkills(Long memberId);

  List<RecruitAndParticipants> getListToEvaluate(Long memberId);

  List<MyPageParticipant> getMyParticipateList(Long memberId);

  List<MyPageRecruit> getMyRecruitList(Long memberId);
}
