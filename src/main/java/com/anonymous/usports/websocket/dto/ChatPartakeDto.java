package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatPartakeDto {
    private Long partakeId;
    private Long chatRoomId;
    private String chatRoomName;
    private Long recruitId;

    public static ChatPartakeDto fromEntity(ChatPartakeEntity chatPartake) {
        return ChatPartakeDto.builder()
                .partakeId(chatPartake.getPartakeId())
                .chatRoomId(chatPartake.getChatRoomEntity().getChatRoomId())
                .chatRoomName(chatPartake.getChatRoomEntity().getChatRoomName())
                .recruitId(chatPartake.getRecruitId())
                .build();
    }
}
