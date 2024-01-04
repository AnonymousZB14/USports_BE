package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.MemberLogin.Response;
import com.anonymous.usports.domain.member.dto.TokenDto;
import com.anonymous.usports.domain.member.dto.frontResponse.MemberResponse;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.CookieService;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import com.anonymous.usports.domain.notification.service.NotificationService;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "OAuth2")
@RestController
@RequestMapping("/oauth2/login")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

  private final MemberServiceImpl memberService;
  private final NotificationService notificationService;
  private final CookieService cookieService;
  private final TokenProvider tokenProvider;

  @GetMapping(value = "/success")
  public ResponseEntity<MemberLogin.Response> oauthLogin(
      @AuthenticationPrincipal MemberDto memberDto,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse
  ) {

    MemberResponse member = memberService.oauthLogin(memberDto.getEmail());

    TokenDto token = tokenProvider.saveTokenInRedis(memberDto.getEmail());

    httpServletResponse.addHeader("Authorization", token.getAccessToken());

    notificationService.checkUnreadNotificationAndSetSession(member.getMemberId(),
        httpServletRequest);

    cookieService.setCookieForLogin(httpServletResponse, token.getAccessToken());

    return ResponseEntity.ok(Response.builder()
        .memberResponse(member)
        .tokenDto(token)
        .build());
  }

}
