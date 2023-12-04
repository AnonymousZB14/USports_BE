package com.anonymous.usports.domain.record.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

  @Query("SELECT r FROM record r JOIN r.member m where m.profileOpen = true and r.sports IN :sportsList order by r.updatedAt DESC")
  List<RecordEntity> findAllOpenProfileRecordsBySportsIn(
      @Param("sportsList") List<SportsEntity> sportsList);

  List<RecordEntity> findAllByMemberIn(List<MemberEntity> member);
}
