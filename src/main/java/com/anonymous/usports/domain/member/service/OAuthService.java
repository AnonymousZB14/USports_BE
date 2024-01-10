package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.kakao.KakaoToken;

public interface OAuthService {

  KakaoToken kakaoGetAccessToken(String authorizationCode);

  MemberDto kakaoLogin(KakaoToken kakaoToken);
}
