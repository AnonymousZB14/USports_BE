package com.anonymous.usports.domain.member.dto.token;

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
public class KakaoToken {

  // 카카오에서 받아오는 access_token이라 응답값의 key와 같아야 한다
  private String token_type;
  private String access_token;
  private int expires_in;
  private String refresh_token;
  private int refresh_token_expires_in;
  private String scope;

}
