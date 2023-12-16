package com.anonymous.usports.domain.mypage.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
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
public class MemberInfo {

  private String profileImage;

  private Long memberId;

  private String name;
  private String accountName;
  private String email;

  private List<String> interestSportsList;
  private int plusAlpha;

  private Double mannerScore;


  public MemberInfo(MemberEntity memberEntity, List<String> interestSportsList, int plusAlpha){
    this.profileImage = memberEntity.getProfileImage();
    this.memberId = memberEntity.getMemberId();
    this.name = memberEntity.getName();
    this.accountName = memberEntity.getAccountName();
    this.email = memberEntity.getEmail();
    this.interestSportsList = interestSportsList;
    this.plusAlpha = plusAlpha;
    this.mannerScore = memberEntity.getMannerScore();
  }

}
