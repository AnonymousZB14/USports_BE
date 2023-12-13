package com.anonymous.usports.domain.mypage.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.dto.MyPageMember;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import java.util.List;

public interface MyPageService {

  MyPageMainDto getMyPageMainData(Long memberId);

  MyPageMember getMyPageMember(MemberEntity member);

  List<SportsSkillDto> getSportsSkills(MemberEntity member);
}
