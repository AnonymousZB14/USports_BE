package com.anonymous.usports.domain.sports.repository;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsRepository extends JpaRepository<SportsEntity, Long> {
  Optional<SportsEntity> findBySportsName(String sportsName);
}
