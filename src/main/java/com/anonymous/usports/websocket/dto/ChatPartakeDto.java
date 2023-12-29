package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatPartakeDto {
    private Long partakeId;
    private Long chatRoomId;
    private String chatRoomName;
    private Long memberId;
    private Long recruitId;
    private String lastReadChatId;
    private Long unreadChatCount;

    public static ChatPartakeDto fromEntity(ChatPartakeEntity chatPartake) {
        return ChatPartakeDto.builder()
                .partakeId(chatPartake.getPartakeId())
                .chatRoomId(chatPartake.getChatRoomEntity().getChatRoomId())
                .chatRoomName(chatPartake.getChatRoomEntity().getChatRoomName())
                .memberId(chatPartake.getMemberEntity().getMemberId())
                .recruitId(chatPartake.getRecruitId())
                .lastReadChatId(chatPartake.getLastReadChatId())
                .build();
    }

    public static ChatPartakeDto fromEntityWithUnreadCount(ChatPartakeEntity chatPartake, long unreadChatCount) {
        return ChatPartakeDto.builder()
            .partakeId(chatPartake.getPartakeId())
            .chatRoomId(chatPartake.getChatRoomEntity().getChatRoomId())
            .chatRoomName(chatPartake.getChatRoomEntity().getChatRoomName())
            .memberId(chatPartake.getMemberEntity().getMemberId())
            .recruitId(chatPartake.getRecruitId())
            .lastReadChatId(chatPartake.getLastReadChatId())
            .unreadChatCount(unreadChatCount)
            .build();
    }
}
