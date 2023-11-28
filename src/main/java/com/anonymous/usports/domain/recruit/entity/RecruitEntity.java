package com.anonymous.usports.domain.recruit.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "recruit")
public class RecruitEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recruitId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sports_id", nullable = false)
  private SportsEntity sports;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "place_name", nullable = false)
  private String placeName;

  @Column(name = "lat", nullable = false)
  private String lat;

  @Column(name = "lnt", nullable = false)
  private String lnt;

  @Column(name = "cost", nullable = false)
  private int cost;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @Column(name = "recruit_count", nullable = false)
  private int recruitCount;

  @Column(name = "meeting_date", nullable = false)
  private LocalDateTime meetingDate;

  @Column(name = "recruit_status", nullable = false)
  private RecruitStatus recruitStatus;

  @Column(name = "grade_from", nullable = false)
  private int gradeFrom;

  @Column(name = "grade_to", nullable = false)
  private int gradeTo;

  @CreatedDate
  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void updateRecruit(RecruitUpdate.Request request, SportsEntity sports){
    this.sports = sports;
    this.title = request.getTitle();
    this.content = request.getContent();
    this.placeName = request.getPlaceName();
    this.lat = request.getLat();
    this.lnt = request.getLnt();
    this.cost = request.getCost();
    this.gender = request.getGender();
    this.recruitCount = request.getRecruitCount();
    this.meetingDate = request.getMeetingDate();
    this.gradeFrom = request.getGradeFrom();
    this.gradeTo = request.getGradeTo();
  }


}
