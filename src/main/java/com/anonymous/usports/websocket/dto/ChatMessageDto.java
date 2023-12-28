package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.entity.ChattingEntity;
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
    private String chatRoomName;
    private String user;
    private Long userId;
    private LocalDateTime time;
    private MessageType type;
    private String imageAddress;

    public static ChattingEntity toEntity(ChatMessageDto chatMessageDto) {
        return ChattingEntity.builder()
            .chatRoomId(chatMessageDto.chatRoomId)
            .content(chatMessageDto.getContent())
            .createdAt(chatMessageDto.time)
            .name(chatMessageDto.user)
            .imageAddress(chatMessageDto.imageAddress)
            .memberId(chatMessageDto.userId)
            .build();
    }
}
