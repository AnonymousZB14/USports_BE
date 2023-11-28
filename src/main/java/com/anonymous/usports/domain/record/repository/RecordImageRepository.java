package com.anonymous.usports.domain.record.repository;

import com.anonymous.usports.domain.record.entity.RecordImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordImageRepository extends JpaRepository<RecordImageEntity, Long> {

}
