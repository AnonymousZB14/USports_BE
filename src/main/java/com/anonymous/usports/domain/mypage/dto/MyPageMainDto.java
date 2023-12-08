package com.anonymous.usports.domain.mypage.dto;

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

  private MyPageMember member;

  private List<SportsSkillDto> sportsSkills;

  private List<RecruitAndParticipants> recruitAndParticipants;

  private List<MyPageParticipant> participantList;

}