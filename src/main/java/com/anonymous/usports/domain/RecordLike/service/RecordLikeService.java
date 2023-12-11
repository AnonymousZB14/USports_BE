package com.anonymous.usports.domain.RecordLike.service;

import com.anonymous.usports.domain.RecordLike.dto.RecordLikeDto;

public interface RecordLikeService {

  RecordLikeDto switchLike(Long recordId, Long loginMemberId);
}
