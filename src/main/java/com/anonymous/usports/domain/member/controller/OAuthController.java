package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.CookieService;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import com.anonymous.usports.domain.notification.service.NotificationService;
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
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

  private final MemberServiceImpl memberService;
  private final NotificationService notificationService;
  private final CookieService cookieService;
  private final TokenProvider tokenProvider;

  @GetMapping("/kakao")
  public ResponseEntity<MemberLogin.Response> kakaoLogin(
    @RequestParam("code") String code
  ) {
    return null;
  }

}
