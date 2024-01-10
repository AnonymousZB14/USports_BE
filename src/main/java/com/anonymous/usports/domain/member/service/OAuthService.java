package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.token.TokenDto;

public interface OAuthService {

  TokenDto kakaoGetAccessToken(String authorizationCode);
}
