package com.anonymous.usports.domain.notification.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

  List<NotificationEntity> findByMemberOrderByCreatedAtDesc(MemberEntity member);

  List<NotificationEntity> findTop100ByCreatedAtBefore(LocalDateTime time);

  boolean existsByMemberAndReadAtIsNull(MemberEntity member);


}
