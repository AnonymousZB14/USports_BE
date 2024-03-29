package com.anonymous.usports.domain.member.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsByAccountName(String accountName);

    boolean existsByEmail(String email);

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByAccountName(String accountName);

    List<MemberEntity> findAllByAccountNameContaining(String accountName);


}
