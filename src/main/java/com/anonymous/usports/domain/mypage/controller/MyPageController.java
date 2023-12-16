package com.anonymous.usports.domain.mypage.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.dto.MyPageParticipant;
import com.anonymous.usports.domain.mypage.dto.MyPageRecruit;
import com.anonymous.usports.domain.mypage.dto.RecruitAndParticipants;
import com.anonymous.usports.domain.mypage.service.MyPageService;
import com.anonymous.usports.domain.profile.dto.MemberProfile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "마이 페이지(My-Page)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MyPageController {

  private final MyPageService myPageService;

  @ApiOperation("마이 페이지")
  @GetMapping("/mypage")
  public ResponseEntity<MyPageMainDto> myPage(@AuthenticationPrincipal MemberDto loginMember) {
    MyPageMainDto myPageMainData = myPageService.getMyPageMainData(loginMember.getMemberId());
    return ResponseEntity.ok(myPageMainData);
  }

  @ApiOperation("마이페이지 - 회원 정보")
  @GetMapping("/mypage/member")
  public ResponseEntity<MemberProfile> memberInfo(
      @AuthenticationPrincipal MemberDto loginMember) {

    MemberProfile memberProfile = MemberProfile.builder()
        .memberInfo(myPageService.getMyPageMember(loginMember.getMemberId()))
        .sportsSkills(myPageService.getSportsSkills(loginMember.getMemberId()))
        .build();

    return ResponseEntity.ok(memberProfile);
  }

  @ApiOperation("마이 페이지 - 평가하기")
  @GetMapping("/mypage/evaluations")
  public ResponseEntity<List<RecruitAndParticipants>> evaluateOthers(
      @AuthenticationPrincipal MemberDto loginMember) {
    List<RecruitAndParticipants> listToEvaluate =
        myPageService.getListToEvaluate(loginMember.getMemberId());
    return ResponseEntity.ok(listToEvaluate);
  }

  @ApiOperation("마이 페이지 - 내 신청 현황")
  @GetMapping("/mypage/participants")
  public ResponseEntity<List<MyPageParticipant>> participateList(
      @AuthenticationPrincipal MemberDto loginMember) {
    List<MyPageParticipant> myParticipateList =
        myPageService.getMyParticipateList(loginMember.getMemberId());
    return ResponseEntity.ok(myParticipateList);
  }

  @ApiOperation("마이 페이지 - 내 모집 관리")
  @GetMapping("/mypage/recruits")
  public ResponseEntity<List<MyPageRecruit>> myRecruitList(
      @AuthenticationPrincipal MemberDto loginMember) {
    List<MyPageRecruit> myRecruitList =
        myPageService.getMyRecruitList(loginMember.getMemberId());
    return ResponseEntity.ok(myRecruitList);
  }


}
