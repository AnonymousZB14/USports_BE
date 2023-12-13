package com.anonymous.usports.domain.participant.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.type.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

  Optional<ParticipantEntity> findByMemberAndRecruitAndStatus(MemberEntity member,
      RecruitEntity recruit, ParticipantStatus status);

  Optional<ParticipantEntity> findByMemberAndRecruit(MemberEntity member, RecruitEntity recruit);

  Page<ParticipantEntity> findAllByRecruitAndStatusOrderByParticipantId(RecruitEntity recruit,
      ParticipantStatus status, Pageable pageable);

  void deleteAllByRecruit(RecruitEntity recruit);

  List<ParticipantEntity> findAllByMemberAndMeetingDateIsAfter(MemberEntity member,
      LocalDateTime datetime);

  List<ParticipantEntity> findAllByRecruitAndStatus(RecruitEntity recruit, ParticipantStatus status);

  List<ParticipantEntity> findTop10ByMemberOrderByRegisteredAtDesc(MemberEntity member);

}
