package com.anonymous.usports.global.constant;

import org.springframework.beans.factory.annotation.Value;

public class KakaoConstant {

  @Value("${kakao.client-id}")
  private String CLIENT_ID;

  @Value("${kakao.client-secret}")
  public static String CLIENT_SECRET;

  @Value("${kakao.redirect-uri}")
  public static String REDIRECT_URL;

  @Value("${kakao.token-uri}")
  public static String ACCESS_TOKEN_URI;

  @Value("${kakao.user-info-uri}")
  public static String USER_INFO_URI;

}
