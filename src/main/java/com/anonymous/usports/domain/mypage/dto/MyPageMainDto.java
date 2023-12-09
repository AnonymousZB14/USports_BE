package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageMainDto {

  private MyPageMember memberProfile;//회원 정보

  private List<SportsSkillDto> sportsSkills;//팝업으로 띄워줄 sportSkill

  private List<RecruitAndParticipants> recruitAndParticipants;//평가하기

  private List<MyPageParticipant> participateList;//내 신청 현황

  private List<MyPageRecruit> myRecruitList;//내 모집 관리

}