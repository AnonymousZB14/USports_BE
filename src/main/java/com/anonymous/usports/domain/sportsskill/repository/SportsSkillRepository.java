package com.anonymous.usports.domain.sportsskill.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsSkillRepository extends JpaRepository<SportsSkillEntity, Long> {
  Optional<SportsSkillEntity> findByMemberAndSports(MemberEntity member, SportsEntity sports);
  List<SportsSkillEntity> findAllByMember(MemberEntity member);

}
