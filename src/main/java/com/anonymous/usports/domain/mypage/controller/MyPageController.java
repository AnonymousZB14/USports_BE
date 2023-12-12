package com.anonymous.usports.domain.mypage.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;
import com.anonymous.usports.domain.mypage.service.MyPageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MyPageController {

  private final MyPageService myPageService;

  @ApiOperation("마이 페이지")
  @GetMapping("/mypage")
  public ResponseEntity<MyPageMainDto> myPage(@AuthenticationPrincipal MemberDto loginMember){
    MyPageMainDto myPageMainData = myPageService.getMyPageMainData(1L);
    return ResponseEntity.ok(myPageMainData);
  }
}
