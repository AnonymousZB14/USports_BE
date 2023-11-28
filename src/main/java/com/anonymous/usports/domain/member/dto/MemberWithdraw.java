package com.anonymous.usports.domain.member.dto;

import lombok.*;

public class MemberWithdraw {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String password;
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
