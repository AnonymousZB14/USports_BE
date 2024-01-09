package com.anonymous.usports.global.constant;

import org.springframework.beans.factory.annotation.Value;

public class OAuthConstant {

  static class Kakao {

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


  }


}
