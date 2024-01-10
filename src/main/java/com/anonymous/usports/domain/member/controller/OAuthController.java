package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;
import com.anonymous.usports.domain.member.service.OAuthService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "OAuth2")
@RestController
@RequestMapping("/login/oauth2/code")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

  private final OAuthService oAuthService;

  @GetMapping("/kakao")
  public ResponseEntity<MemberLogin.Response> kakaoLogin(
    @RequestParam("code") String code
  ) {

    // 프론트에서 받아온 인가 코드를 통해 카카오 token을 받는다
    KakaoToken kakaoToken = oAuthService.kakaoGetAccessToken(code);

    log.info("{}", kakaoToken);

    // 토큰을 가지고 유저 정보를 통해, 회원가입을 시키거나 로그인을 시킨다
    MemberDto memberDto = oAuthService.kakaoLogin(kakaoToken);




    return null;
  }

}
