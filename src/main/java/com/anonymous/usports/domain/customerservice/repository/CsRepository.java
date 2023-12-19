package com.anonymous.usports.domain.customerservice.repository;

import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.CsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CsRepository extends JpaRepository<CsEntity, Long> {
  Page<CsEntity> findAllByMemberEntityOrderByUpdatedAtDesc(MemberEntity member, Pageable pageable);

  @Query(value = "SELECT cs FROM customer_service cs"
      + " WHERE (:member IS NULL OR cs.memberEntity = :member)"
      + " AND (:status IS NULL OR cs.csStatus = :status)"
      + "ORDER BY cs.updatedAt DESC")
  Page<CsEntity> findALlByConditionsFromAdmin(
      @Param("member") MemberEntity member,
      @Param("status") CsStatus csStatus,
      Pageable pageable
  );
}
