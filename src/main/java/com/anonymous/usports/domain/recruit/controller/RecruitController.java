package com.anonymous.usports.domain.recruit.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.recruit.dto.RecruitDeleteResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitEndResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;
import com.anonymous.usports.domain.recruit.dto.RecruitResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import com.anonymous.usports.global.type.Gender;
import io.swagger.annotations.Api;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "모집 글(Recruit)")
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
  public ResponseEntity<RecruitRegister.Response> registerRecruit(@RequestBody RecruitRegister.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {

    RecruitDto result = recruitService.registerRecruit(request, loginMember.getMemberId());

    return ResponseEntity.ok(new RecruitRegister.Response(result));
  }

  @ApiOperation("운동 모집 게시글 한 건 조회하기")
  @GetMapping("/recruit/{recruitId}")
  public ResponseEntity<RecruitResponse> getRecruit(@PathVariable Long recruitId) {
    RecruitResponse result = recruitService.getRecruit(recruitId);
    return ResponseEntity.ok(result);
  }

  @ApiOperation("운동 모집 게시글 수정 페이지")
  @GetMapping("/recruit/{recruitId}/update")
  public ResponseEntity<?> updateRecruitPage(@PathVariable Long recruitId) {
    //TODO
    return ResponseEntity.ok(null);
  }

  @ApiOperation("운동 모집 게시글 수정")
  @PutMapping("/recruit/{recruitId}")
  public ResponseEntity<RecruitUpdate.Response> updateRecruit(@PathVariable Long recruitId,
      @RequestBody RecruitUpdate.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {
    RecruitDto result = recruitService.updateRecruit(request, recruitId, loginMember.getMemberId());
    return ResponseEntity.ok(new RecruitUpdate.Response(result));
  }

  @ApiOperation("운동 모집 게시글 삭제")
  @DeleteMapping("/recruit/{recruitId}")
  public ResponseEntity<RecruitDeleteResponse> deleteRecruit(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {

    RecruitDto result = recruitService.deleteRecruit(recruitId, loginMember.getMemberId());
    return ResponseEntity.ok(new RecruitDeleteResponse(result));
  }

  @ApiOperation("운동 모집 마감 / 마감 취소")
  @PutMapping ("/recruit/{recruitId}/end")
  public ResponseEntity<RecruitEndResponse> endRecruiting(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {

    RecruitEndResponse response = recruitService.endRecruit(recruitId,
        loginMember.getMemberId());
    return ResponseEntity.ok(response);
  }

  @ApiOperation("운동 모집글 검색")
  @GetMapping("/recruits")
  public ResponseEntity<RecruitListDto> getRecruitList(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String region,
      @RequestParam(required = false) String sports,
      @RequestParam(required = false) Gender gender,
      @RequestParam(required = false) boolean closeInclude
      ){

    RecruitListDto result =
        recruitService.getRecruitsByConditions(page, search, region, sports, gender, closeInclude);

    return ResponseEntity.ok(result);
  }


}
