package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.TokenDto;

public interface OAuthService {

  TokenDto kakaoGetAccessToken(String authorizationCode);
}
