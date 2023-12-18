package com.anonymous.usports.domain.member.dto;

import java.util.List;
import lombok.*;

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
        private MemberDto member;
        private List<String> interestSportsList;
    }
}
