package com.anonymous.usports.domain.participant.repository;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

}
