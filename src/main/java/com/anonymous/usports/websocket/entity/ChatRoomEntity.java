package com.anonymous.usports.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity(name="chat_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long chatRoomId;

    @Column(name = "chatroom_name", length = 50, nullable = false)
    private String chatroomName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomEntity that = (ChatRoomEntity) o;
        return Objects.equals(chatRoomId, that.chatRoomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId);
    }
}
