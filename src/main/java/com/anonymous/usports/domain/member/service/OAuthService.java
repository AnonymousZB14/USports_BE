package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.frontResponse.MemberResponse;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;

public interface OAuthService {

  KakaoToken kakaoGetAccessToken(String authorizationCode);

  MemberResponse kakaoLogin(KakaoToken kakaoToken);
}
