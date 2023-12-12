package com.anonymous.usports.domain.RecordLike.repository;

import com.anonymous.usports.domain.RecordLike.entity.RecordLikeEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLikeEntity, Long> {

  RecordLikeEntity findByRecordAndMember(RecordEntity record, MemberEntity loginMember);

}
