package com.anonymous.usports.domain.recruit.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.recruit.dto.RecruitDeleteResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecruitController {

  private final RecruitService recruitService;

  @ApiOperation("운동 모집 게시글 등록 페이지")
  @GetMapping("/recruit")
  public ResponseEntity<?> registerRecruitPage() {
    //TODO
    return ResponseEntity.ok(null);
  }

  @ApiOperation("운동 모집 게시글 등록하기")
  @PostMapping("/recruit")
  public ResponseEntity<?> registerRecruit(@RequestBody RecruitRegister.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {

    RecruitDto result = recruitService.registerRecruit(request, loginMember.getMemberId());

    return ResponseEntity.ok(new RecruitRegister.Response(result));
  }

  @ApiOperation("운동 모집 게시글 한 건 조회하기")
  @GetMapping("/recruit/{recruitId}")
  public ResponseEntity<?> getRecruit(@PathVariable Long recruitId) {
    RecruitDto result = recruitService.getRecruit(recruitId);
    return ResponseEntity.ok(result);
  }

  @ApiOperation("운동 모집 게시글 수정 페이지")
  @GetMapping("/recruit/{recruitId}/update")
  public ResponseEntity<?> updateRecruitPage(@PathVariable Long recruitId) {
    return ResponseEntity.ok(null);
  }

  @ApiOperation("운동 모집 게시글 수정")
  @PutMapping("/recruit/{recruitId}")
  public ResponseEntity<?> updateRecruit(@PathVariable Long recruitId,
      @RequestBody RecruitUpdate.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {
    RecruitDto result = recruitService.updateRecruit(request, recruitId, loginMember.getMemberId());
    return ResponseEntity.ok(new RecruitUpdate.Response(result));
  }

  @ApiOperation("운동 모집 게시글 삭제")
  @DeleteMapping("/recruit/{recruitId}")
  public ResponseEntity<?> deleteRecruit(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {

    RecruitDto result = recruitService.deleteRecruit(recruitId, loginMember.getMemberId());
    return ResponseEntity.ok(new RecruitDeleteResponse(result));
  }

}
