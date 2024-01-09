package com.anonymous.usports.domain.member.dto;


import com.anonymous.usports.domain.member.entity.MemberEntity;
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
public class MemberSearchResponse {

  private Long memberId;

  private String profileImage;

  private String accountName;

  private String name;

  private String email;

  public static MemberSearchResponse fromEntity(MemberEntity memberEntity){
    return MemberSearchResponse.builder()
        .memberId(memberEntity.getMemberId())
        .profileImage(memberEntity.getProfileImage())
        .accountName(memberEntity.getAccountName())
        .name(memberEntity.getName())
        .email(memberEntity.getEmail())
        .build();
  }
}
