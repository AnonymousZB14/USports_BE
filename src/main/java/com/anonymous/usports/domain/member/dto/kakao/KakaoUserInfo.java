package com.anonymous.usports.domain.member.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoUserInfo {

  public Long id;
  public String connected_at;
  public KakaoAccount kakao_account;
  public Properties properties;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class KakaoAccount {
    public boolean profile_nickname_needs_agreement;

    public Profile profile;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Profile {
      public String nickname;

    }

    public boolean has_email;
    public boolean email_needs_agreement;
    public boolean is_email_valid;
    public boolean is_email_verified;
    public String email;

    public boolean has_gender;
    public boolean gender_needs_agreement;
    public String gender;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Properties {
    public String nickname;
  }
}
