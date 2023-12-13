package com.anonymous.usports.websocket.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private String chatRoomId;
    private String chatRoomName;
    private Long userCount;

}
