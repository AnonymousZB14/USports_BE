package com.anonymous.usports.domain.record.repository;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.entity.RecordImageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordImageRepository extends JpaRepository<RecordImageEntity, Long> {

  List<RecordImageEntity> findAllByRecord(RecordEntity record);

}
