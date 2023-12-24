package com.anonymous.usports.domain.member.dto;

import lombok.*;

import javax.validation.constraints.Pattern;

public class PasswordLostResponse {


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @Pattern(message="이메일 형식에 맞지 않습니다",
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
        private String email;
        private String name;

        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
                message = "010-0000-0000 형식으로 입력해주세요")
        private String phoneNumber;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String message;
    }
}
