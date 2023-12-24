package com.anonymous.usports.domain.recordlike.service;

import com.anonymous.usports.domain.recordlike.dto.RecordLikeDto;

public interface RecordLikeService {

  RecordLikeDto switchLikeOrCancel(Long recordId, Long loginMemberId);
}
