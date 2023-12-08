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

  private RecruitDto recruitDto;
  private List<MemberDto> memberList;

  public RecruitAndParticipants(RecruitDto recruitDto, List<MemberDto> memberList) {
    this.recruitDto = recruitDto;
    this.memberList = memberList;
  }
}
