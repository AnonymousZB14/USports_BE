package com.anonymous.usports.websocket.repository;

import com.anonymous.usports.websocket.entity.ChattingEntity;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChattingRepository extends MongoRepository<ChattingEntity, String> {

  Page<ChattingEntity> findAllByChatRoomId(Long chatRoomId, Pageable pageable);

  ChattingEntity findTopByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

  //  @Query("{ 'chatRoomId' : ?0, '_id' : { $gt : { '$oid': ?1 } } }") String을 사용할 경우 이 쿼리가 필요함. String을 objectId로 변환
  long countAllByChatRoomIdAndIdGreaterThan(Long chatRoomId, ObjectId lastReadChatId);

}
