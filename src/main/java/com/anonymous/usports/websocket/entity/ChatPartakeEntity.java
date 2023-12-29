package com.anonymous.usports.websocket.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "lastReadChatId")
    private String lastReadChatId;

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
