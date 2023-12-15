package com.anonymous.usports.websocket.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatEnterDto {

    private Long chatRoomId;
    private String chatRoomName;
    private String username;

}
