package com.anonymous.usports.websocket.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.websocket.entity.ChatPartakeEntity;
import com.anonymous.usports.websocket.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatPartakeRepository extends JpaRepository<ChatPartakeEntity, Long> {

    List<ChatPartakeEntity> findAllByMemberEntityInAndRecruitIdIsNull(List<MemberEntity> member);

    Optional<ChatPartakeEntity> findByChatRoomEntityAndMemberEntity(ChatRoomEntity chatRoom, MemberEntity member);

    List<ChatPartakeEntity> findAllByMemberEntity(MemberEntity member);

    boolean existsByRecruitId(Long recruitId);

    boolean existsByChatRoomEntityAndMemberEntity(ChatRoomEntity chatRoom, MemberEntity member);

    boolean existsByRecruitIdAndMemberEntity(Long recruitId, MemberEntity member);

}
