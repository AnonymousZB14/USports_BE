package com.anonymous.usports.domain.member.dto.token;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
