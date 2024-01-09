package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.TokenDto;
import com.anonymous.usports.domain.member.service.OAuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OAuthServiceImpl implements OAuthService {

  @Override
  public TokenDto kakaoGetAccessToken(String authorizationCode) {

    WebClient webClient = WebClient.builder()
        .baseUrl("https://kauth.kakao.com/oauth/token")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .build();

    webClient.post()
        .body("grant_type", "authorization_code")



    return null;
  }
}
