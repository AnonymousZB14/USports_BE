package com.anonymous.usports.domain.record.repository;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

}
