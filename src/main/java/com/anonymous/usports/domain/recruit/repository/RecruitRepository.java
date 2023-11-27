package com.anonymous.usports.domain.recruit.repository;

import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitRepository extends JpaRepository<RecruitEntity, Long> {

}
