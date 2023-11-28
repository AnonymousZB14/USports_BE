package com.anonymous.usports.domain.follow.service;

import com.anonymous.usports.domain.follow.dto.FollowDto;

public interface FollowService {

  FollowDto followMember(Long fromMemberId, Long toMemberId);

  FollowDto deleteFollow(Long followId, Long memberId);
}
