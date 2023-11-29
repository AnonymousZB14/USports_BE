package com.anonymous.usports.domain.follow.service;

import com.anonymous.usports.domain.follow.dto.FollowDto;

public interface FollowService {

  FollowDto changeFollow(Long fromMemberId, Long toMemberId);
}
