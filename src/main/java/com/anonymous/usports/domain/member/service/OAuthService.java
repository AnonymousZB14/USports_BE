package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;

public interface OAuthService {

  KakaoToken kakaoGetAccessToken(String authorizationCode);

  MemberLogin.Response kakaoLogin(KakaoToken kakaoToken);
}
