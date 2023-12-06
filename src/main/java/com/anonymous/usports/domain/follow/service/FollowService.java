package com.anonymous.usports.domain.follow.service;

import com.anonymous.usports.domain.follow.dto.FollowListDto;
import com.anonymous.usports.domain.follow.dto.FollowResponse;
import com.anonymous.usports.global.type.FollowDecisionType;
import com.anonymous.usports.global.type.FollowListType;

public interface FollowService {

  FollowResponse changeFollow(Long loginMemberId, Long toMemberId);

  FollowListDto getFollowPage(FollowListType type, int page, Long loginMemberId);

  FollowResponse manageFollow(Long fromMemberId, Long loginMemberId, FollowDecisionType decision);
}
