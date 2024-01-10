package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.token.TokenDto;
import com.anonymous.usports.domain.member.service.OAuthService;

import com.anonymous.usports.global.constant.KakaoConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class OAuthServiceImpl implements OAuthService {

  @Override
  public TokenDto kakaoGetAccessToken(String authorizationCode) {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", KakaoConstant.CLIENT_ID);
    params.add("redirect_uri", KakaoConstant.REDIRECT_URL);
    params.add("code", authorizationCode);
    params.add("client_secret", KakaoConstant.CLIENT_SECRET);

    WebClient webClient = WebClient.create(KakaoConstant.ACCESS_TOKEN_URI);
    String response = webClient.post()
        .uri(KakaoConstant.ACCESS_TOKEN_URI)
        .body(BodyInserters.fromFormData(params))
        .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
        .retrieve()
        .bodyToMono(String.class)
        .block();

    log.info(response);

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
}
