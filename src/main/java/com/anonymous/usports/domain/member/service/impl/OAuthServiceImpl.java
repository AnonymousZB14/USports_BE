package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;
import com.anonymous.usports.domain.member.dto.kakao.KakaoUserInfo;
import com.anonymous.usports.domain.member.service.OAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class OAuthServiceImpl implements OAuthService {

  @Value("${kakao.client-id}")
  private String CLIENT_ID;

  @Value("${kakao.client-secret}")
  private String CLIENT_SECRET;

  @Value("${kakao.redirect-uri}")
  private String REDIRECT_URL;

  @Value("${kakao.token-uri}")
  private String ACCESS_TOKEN_URI;

  @Value("${kakao.user-info-uri}")
  private String USER_INFO_URI;

  /**
   * 카카오 token 발급 받기
   */
  @Override
  public KakaoToken kakaoGetAccessToken(String authorizationCode) {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", CLIENT_ID);
    params.add("redirect_uri", REDIRECT_URL);
    params.add("code", authorizationCode);
    params.add("client_secret", CLIENT_SECRET);

    WebClient webClient = WebClient.create(ACCESS_TOKEN_URI);
    String response = webClient.post()
        .uri(ACCESS_TOKEN_URI)
        .body(BodyInserters.fromFormData(params))
        .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .retrieve()
        .bodyToMono(String.class)
        .block();

    // json 형태로 변환
    ObjectMapper objectMapper = new ObjectMapper();
    KakaoToken kakaoToken = null;

    try {
      kakaoToken = objectMapper.readValue(response, KakaoToken.class);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return kakaoToken;
  }

  /**
   * 카카오 로그인 관련
   */

  private KakaoUserInfo getProfile(KakaoToken kakaoToken) {

    WebClient webClient = WebClient.create(USER_INFO_URI);
    String response = webClient.post()
        .uri(USER_INFO_URI)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken.getAccess_token())
        .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .retrieve()
        .bodyToMono(String.class)
        .block();

    log.info("response : {}", response);

    ObjectMapper objectMapper = new ObjectMapper();
    KakaoUserInfo kakaoUserInfo = null;

    try {
      kakaoUserInfo = objectMapper.readValue(response, KakaoUserInfo.class);

      log.info("kakaoUserInfo : {}", kakaoUserInfo);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return kakaoUserInfo;
  }

  @Override
  public MemberLogin.Response kakaoLogin(KakaoToken kakaoToken) {

    KakaoUserInfo userInfo = getProfile(kakaoToken);

    return null;
  }
}
