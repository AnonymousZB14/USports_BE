package com.anonymous.usports.websocket.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "chat_partake")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ChatPartakeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partakeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatRoomEntity;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Column(name = "recruit_id")
    private Long recruitId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPartakeEntity that = (ChatPartakeEntity) o;
        return Objects.equals(partakeId, that.partakeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partakeId);
    }
}
