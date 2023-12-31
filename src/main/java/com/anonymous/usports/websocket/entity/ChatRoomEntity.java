package com.anonymous.usports.websocket.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity(name="chat_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long chatRoomId;

    @Column(name = "chatroom_name", length = 50, nullable = false)
    private String chatRoomName;

    @Column(name = "user_count")
    private Long userCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomEntity chatRoom = (ChatRoomEntity) o;
        return Objects.equals(chatRoomId, chatRoom.chatRoomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId);
    }
}
