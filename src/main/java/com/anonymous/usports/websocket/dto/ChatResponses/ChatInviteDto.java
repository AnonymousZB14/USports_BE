package com.anonymous.usports.websocket.dto.ChatResponses;

import lombok.*;

public class ChatInviteDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long chatId;
        private Long recruitId;
        private Long memberId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long chatRoomId;
        private String message;
    }
}
