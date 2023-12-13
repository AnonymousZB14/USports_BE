package com.anonymous.usports.websocket.dto.httpbody;

import lombok.*;

public class CreateRecruitChat {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long recruitId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String message;
    }
}
