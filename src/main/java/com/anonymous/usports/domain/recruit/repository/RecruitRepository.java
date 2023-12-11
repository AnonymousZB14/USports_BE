package com.anonymous.usports.domain.recruit.repository;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.type.Gender;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitRepository extends JpaRepository<RecruitEntity, Long>{


  @Query("SELECT r from recruit r "
      + " WHERE (:search is null or r.title like concat('%', :search, '%')) "
      + " and (:region is null or r.region = :region) "
      + " and (:sports is null or r.sports = :sports) "
      + " and (:gender is null or r.gender = :gender)")
  Page<RecruitEntity> findAllByConditionIncludeEND(
      @Param("search") String search,
      @Param("region") String region,
      @Param("sports") SportsEntity sports,
      @Param("gender") Gender gender,
      Pageable pageable);

  @Query("SELECT r from recruit r "
      + " WHERE (:search is null or r.title like concat('%', :search, '%')) "
      + " and (:region is null or r.region = :region) "
      + " and (:sports is null or r.sports = :sports) "
      + " and (:gender is null or r.gender = :gender) "
      + " and (r.recruitStatus != 'END')")
  Page<RecruitEntity> findAllByConditionNotIncludeEND(
      @Param("search") String search,
      @Param("region") String region,
      @Param("sports") SportsEntity sports,
      @Param("gender") Gender gender,
      Pageable pageable);

  List<RecruitEntity> findAllByMemberAndMeetingDateIsAfter(MemberEntity member, LocalDateTime time);
}
