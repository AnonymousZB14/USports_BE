package com.anonymous.usports.domain.member.repository;

import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestedSportsRepository extends JpaRepository<InterestedSportsEntity, Long> {

    void deleteAllByMemberEntity(MemberEntity memberEntity);


    List<InterestedSportsEntity> findAllByMemberEntity(MemberEntity memberEntity);
}
