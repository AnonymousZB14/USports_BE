package com.anonymous.usports.websocket.dto.ChatResponses;

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
        private Long chatRoomId;
        private String message;
    }
}
