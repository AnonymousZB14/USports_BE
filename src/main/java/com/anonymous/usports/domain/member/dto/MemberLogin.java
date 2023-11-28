package com.anonymous.usports.domain.member.dto;

import lombok.*;

public class MemberLogin {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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
    }
}
