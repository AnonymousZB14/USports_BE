package com.anonymous.usports.domain.evaluation.repository;

import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Long> {

  boolean existsByRecruitAndFromMemberAndToMember(RecruitEntity recruit, MemberEntity fromMember, MemberEntity toMember);
}
