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
    private String sender;
    private Long senderId;
    private LocalDateTime time;
    private MessageType type;
    private String imageAddress;

    public static ChattingEntity toEntity(ChatMessageDto chatMessageDto) {
        return ChattingEntity.builder()
            .chatRoomId(chatMessageDto.chatRoomId)
            .content(chatMessageDto.getContent())
            .createdAt(chatMessageDto.time)
            .name(chatMessageDto.sender)
            .imageAddress(chatMessageDto.imageAddress)
            .memberId(chatMessageDto.senderId)
            .build();
    }
}
