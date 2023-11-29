package com.anonymous.usports.domain.follow.service;

import com.anonymous.usports.domain.follow.dto.FollowListDto;
import com.anonymous.usports.domain.follow.dto.FollowResponse;
import com.anonymous.usports.global.type.FollowDecisionType;
import com.anonymous.usports.global.type.FollowListType;

public interface FollowService {

  FollowResponse changeFollow(Long fromMemberId, Long toMemberId);

  FollowListDto getFollowList(FollowListType type, int page, Long memberId);

  FollowResponse manageFollow(Long followId, Long memberId, FollowDecisionType decision);
}
