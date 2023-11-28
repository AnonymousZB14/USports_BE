package com.anonymous.usports.domain.participant.repository;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {
  Optional<ParticipantEntity> findByMemberIdAndRecruitId(Long memberId, Long recruitId);
  Page<ParticipantEntity> findAllByRecruitId(Long recruitId, Pageable pageable);
}
