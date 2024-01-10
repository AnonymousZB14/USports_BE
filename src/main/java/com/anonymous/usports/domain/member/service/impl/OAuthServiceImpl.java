package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;
import com.anonymous.usports.domain.member.dto.kakao.KakaoUserInfo;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.OAuthService;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

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
  public MemberDto kakaoLogin(KakaoToken kakaoToken) {

    KakaoUserInfo userInfo = getProfile(kakaoToken);

    String nickName = userInfo.properties.getNickname();
    String email = userInfo.kakao_account.getEmail();
    String gender = userInfo.kakao_account.getGender();

    Optional<MemberEntity> member = memberRepository.findByEmail(email);

    MemberEntity memberEntity = null;

    if (member.isEmpty()) {
      log.info("{} : 새로운 유저입니다", email);

      Gender saveGender = null;
      if (gender.equals("male")) saveGender = Gender.MALE;
      else if (gender.equals("famale")) saveGender = Gender.FEMALE;

      StringBuilder tempAccountName = new StringBuilder();
      tempAccountName.append(email.split("@")[0]);
      tempAccountName.append("%");
      tempAccountName.append(UUID.randomUUID().toString().substring(0,6));
      log.info("{}", tempAccountName);

      memberEntity = memberRepository.save(
          MemberEntity.builder()
              .accountName(tempAccountName.toString())
              .name(nickName)
              .email(email)
              .emailAuthAt(LocalDateTime.now())
              .password(passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 10)))
              .gender(saveGender)
              .profileOpen(false)
              .loginBy(LoginBy.KAKAO)
              .role(Role.UNAUTH)
              .build());
    } else {
      memberEntity = member.get();

      MemberDto memberDto = MemberDto.fromEntity(memberEntity);

      if (memberDto.getEmailAuthAt() == null) {
        memberEntity.setEmailAuthAt(LocalDateTime.now());
        memberEntity = memberRepository.save(memberEntity);
      }
    }

    return MemberDto.fromEntity(memberEntity);
  }
}
