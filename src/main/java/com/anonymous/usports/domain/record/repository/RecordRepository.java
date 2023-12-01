package com.anonymous.usports.domain.record.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

  Page<RecordEntity> findAllBySportsIn(List<SportsEntity> collect, PageRequest pageRequest);

  List<RecordEntity> findAllByMemberIn(List<MemberEntity> member);
}
