package com.anonymous.usports.domain.follow.repository;

import com.anonymous.usports.domain.follow.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

}
