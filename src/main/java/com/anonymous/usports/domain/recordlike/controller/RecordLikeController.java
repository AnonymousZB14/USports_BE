package com.anonymous.usports.domain.recordlike.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.recordlike.dto.RecordLikeDto;
import com.anonymous.usports.domain.recordlike.service.RecordLikeService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecordLikeController {

  private final RecordLikeService recordLikeService;

  @ApiOperation("좋아요 신청/취소")
  @PostMapping("/record/{recordId}/like")
  public ResponseEntity<RecordLikeDto> switchLikeOrCancel(
      @PathVariable Long recordId,
      @AuthenticationPrincipal MemberDto loginMember
  ) {
    RecordLikeDto response = recordLikeService.switchLikeOrCancel(recordId,
        loginMember.getMemberId());
    return ResponseEntity.ok(response);
  }
}
