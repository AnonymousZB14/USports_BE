package com.anonymous.usports.domain.profile.dto;

import com.anonymous.usports.domain.mypage.dto.MyPageMember;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;
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
public class ProfileRecruits {

  private MyPageMember myPageMember;

  private List<SportsSkillDto> sportsSkills;

  private RecruitListDto recruitList;
}
