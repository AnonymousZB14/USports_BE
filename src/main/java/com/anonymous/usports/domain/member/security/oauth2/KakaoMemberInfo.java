package com.anonymous.usports.domain.member.security.oauth2;

import java.util.Map;

public class KakaoMemberInfo implements OAuth2MemberInfo{

    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> profileAttributes;

    public KakaoMemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) kakaoAccountAttributes.get("profile");

    }


    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return kakaoAccountAttributes.get("name").toString();
    }

    @Override
    public String getNickName() {
        return profileAttributes.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }

    @Override
    public String getProfileImage() {
        return profileAttributes.get("profile_image").toString();
    }

    @Override
    public String getBirthDay() {
        return kakaoAccountAttributes.get("birthday").toString();
    }

    @Override
    public String getGender() {
        return kakaoAccountAttributes.get("gender").toString();
    }
}
