package com.anonymous.usports.domain.recordlike.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.recordlike.entity.RecordLikeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLikeEntity, Long> {

  Optional<RecordLikeEntity> findByRecordAndMember(RecordEntity record, MemberEntity loginMember);

  void deleteAllByRecord(RecordEntity recordEntity);

  boolean existsByRecordAndMember(RecordEntity record, MemberEntity loginMember);

}
