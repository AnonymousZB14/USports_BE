package com.anonymous.usports.websocket.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatPartakeRepository extends JpaRepository<ChatPartakeEntity, Long> {

    List<ChatPartakeEntity> findAllByMemberEntityInAndRecruitIdIsNull(List<MemberEntity> member);

    List<ChatPartakeEntity> findAllByChatRoomEntity(ChatRoomEntity chatRoom);

    Optional<ChatPartakeEntity> findByChatRoomEntityAndMemberEntity(ChatRoomEntity chatRoom, MemberEntity member);

    List<ChatPartakeEntity> findAllByMemberEntity(MemberEntity member);

    boolean existsByRecruitId(Long recruitId);

    boolean existsByChatRoomEntityAndMemberEntity(ChatRoomEntity chatRoom, MemberEntity member);

    boolean existsByRecruitIdAndChatRoomEntityAndMemberEntity(Long recruitId, ChatRoomEntity chatRoom, MemberEntity member);

}
