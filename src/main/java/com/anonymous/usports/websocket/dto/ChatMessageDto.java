package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.type.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {

    private String content; // 내용
    private Long chatRoomId;
    private String sender;
    private LocalDateTime time;
    private MessageType type;
}
