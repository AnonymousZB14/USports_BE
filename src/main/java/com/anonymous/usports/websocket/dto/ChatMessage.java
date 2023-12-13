package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.type.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String content; // 내용
    private String sender;
//    private ChatRoomEntity chatRoomEntity;
    private MessageType type;
}
