package com.anonymous.usports.domain.member.security.oauth2;

public interface OAuth2MemberInfo {
    String getProviderId(); //공급자 아이디 ex) google, facebook
    String getProvider(); //공급자 ex) google, facebook
    String getNickName();
    String getEmail(); //사용자 이메일 ex) gildong@gmail.com
    String getGender();


}
