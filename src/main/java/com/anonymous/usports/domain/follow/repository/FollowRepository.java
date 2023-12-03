package com.anonymous.usports.domain.follow.repository;

import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.FollowStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {



  Optional<FollowEntity> findByFromMemberAndToMemberAndFollowStatus(MemberEntity fromMember,
      MemberEntity toMemberId, FollowStatus followStatus);

  Page<FollowEntity> findAllByFromMemberAndFollowStatusOrderByFollowDateDesc(MemberEntity member, FollowStatus followStatus, Pageable pageable);

  Page<FollowEntity> findAllByToMemberAndFollowStatusOrderByFollowDateDesc(MemberEntity member, FollowStatus followStatus, Pageable pageable);

  Page<FollowEntity> findAllByToMemberAndFollowStatus(MemberEntity member, FollowStatus followStatus, Pageable pageable);

  Optional<FollowEntity> findByFromMemberAndToMember(MemberEntity fromMember, MemberEntity toMember);

  List<FollowEntity> findAllByFromMemberAndFollowStatus(MemberEntity member, FollowStatus followStatus);
}
