package com.anonymous.usports.domain.member.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsByAccountName(String accountName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}
