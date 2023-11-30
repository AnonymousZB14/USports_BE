package com.anonymous.usports.domain.participant.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {
  Optional<ParticipantEntity> findByMemberAndRecruit(MemberEntity member, RecruitEntity recruit);
  boolean existsByMemberAndRecruit(MemberEntity member, RecruitEntity recruit);
  Page<ParticipantEntity> findAllByRecruitOrderByParticipantId(RecruitEntity recruit, Pageable pageable);
}
