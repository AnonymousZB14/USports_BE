package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RecruitAndParticipants {

  private RecruitDto recruit;
  private List<MemberDto> memberList;

  public RecruitAndParticipants(RecruitDto recruit, List<MemberDto> memberList) {
    this.recruit = recruit;
    this.memberList = memberList;
  }
}
