package com.anonymous.usports.domain.follow.repository;

import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

  Optional<FollowEntity> findByFromMemberAndToMemberAndConsentDateIsNotNull(MemberEntity fromMember,
      MemberEntity toMemberId);

  Page<FollowEntity> findAllByFromMemberAndConsentDateIsNotNull(MemberEntity member,
      Pageable pageable);

  Page<FollowEntity> findAllByToMemberAndConsentDateIsNotNull(MemberEntity member,
      Pageable pageable);

  Page<FollowEntity> findAllByToMemberAndConsentDateIsNull(MemberEntity member, Pageable pageable);
}
