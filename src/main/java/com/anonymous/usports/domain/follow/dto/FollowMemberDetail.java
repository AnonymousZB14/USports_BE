package com.anonymous.usports.domain.follow.dto;

import com.anonymous.usports.domain.follow.entity.FollowEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowMemberDetail {
  private Long followId;
  private Long fromMemberId;
  private Long toMemberId;
  private String fromMemberAccountName;
  private String toMemberAccountName;

  public static FollowMemberDetail fromEntity(FollowEntity followEntity) {
    return FollowMemberDetail.builder()
        .followId(followEntity.getFollowId())
        .fromMemberId(followEntity.getFromMember().getMemberId())
        .toMemberId(followEntity.getToMember().getMemberId())
        .fromMemberAccountName(followEntity.getFromMember().getAccountName())
        .toMemberAccountName(followEntity.getToMember().getAccountName())
        .build();
  }
}
