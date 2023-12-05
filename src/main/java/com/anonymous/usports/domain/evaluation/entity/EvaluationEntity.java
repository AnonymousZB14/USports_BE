package com.anonymous.usports.domain.evaluation.entity;


import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "evaluation")
public class EvaluationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "evaluation_id", nullable = false)
  private Long evaluationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruit_id", nullable = false)
  private RecruitEntity recruit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_member_id", nullable = false)
  private MemberEntity fromMember; //평가자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_member_id", nullable = false)
  private MemberEntity toMember; //피평가자

  @Column(name = "kindness", nullable = false)
  private int kindness;

  @Column(name = "passion", nullable = false)
  private int passion;

  @Column(name = "teamwork", nullable = false)
  private int teamwork;

  @Column(name = "sports_score", nullable = false)
  private int sportsScore;

  @CreatedDate
  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt; //평가 일시

}
