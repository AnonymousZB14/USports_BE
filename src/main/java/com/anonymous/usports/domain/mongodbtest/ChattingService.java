package com.anonymous.usports.domain.mongodbtest;

public interface ChattingService {
  ChattingDto save(Long memberId, ChattingInput input);
}
