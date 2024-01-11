package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.entity.ChattingEntity;
import com.anonymous.usports.websocket.type.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponseDto {

    private String content; // 내용
    private Long chatRoomId;
    private String chatRoomName;
    private String user;
    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime time;
    private MessageType type;
    private String imageAddress;

    public static ChattingEntity toEntity(ChatMessageResponseDto chatMessageDto) {
        return ChattingEntity.builder()
            .chatRoomId(chatMessageDto.getChatRoomId())
            .content(chatMessageDto.getContent())
            .createdAt(chatMessageDto.getTime())
            .memberId(chatMessageDto.getUserId())
            .build();
    }
}
