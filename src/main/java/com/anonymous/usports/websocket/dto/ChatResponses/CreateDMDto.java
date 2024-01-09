package com.anonymous.usports.websocket.dto.ChatResponses;

import lombok.*;

public class CreateDMDto {

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long memberId;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long chatRoomId;
        private String message;
    }
}
