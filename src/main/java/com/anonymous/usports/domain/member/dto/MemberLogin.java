package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.dto.frontResponse.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberLogin {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private TokenDto tokenDto;
        private MemberResponse memberResponse;

    }
}
