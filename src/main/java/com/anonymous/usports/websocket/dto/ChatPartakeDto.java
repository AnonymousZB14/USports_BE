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
    private Long memberId;
    private Long recruitId;

    public static ChatPartakeDto fromEntity(ChatPartakeEntity chatPartake) {
        return ChatPartakeDto.builder()
                .partakeId(chatPartake.getPartakeId())
                .chatRoomId(chatPartake.getChatRoomEntity().getChatRoomId())
                .memberId(chatPartake.getMemberEntity().getMemberId())
                .recruitId(chatPartake.getRecruitId())
                .build();
    }
}
