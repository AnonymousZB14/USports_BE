package com.anonymous.usports.domain.customerservice.repository;

import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsRepository extends JpaRepository<CsEntity, Long> {

}
