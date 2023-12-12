package com.anonymous.usports.domain.chatting.repository;

import com.anonymous.usports.domain.chatting.entity.ChattingEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRepository extends MongoRepository<ChattingEntity, String> {
  List<ChattingEntity> findAllByChatRoomId(Long chatRoomId);
}
