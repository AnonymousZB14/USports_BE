package com.anonymous.usports.domain.sportsskill.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SportsSkillEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sportsSkillId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sports_id", nullable = false)
  private SportsEntity sports;

  @Column(name = "sports_score", nullable = false)
  private Long sportsScore;

  @Column(name = "evaluate_count", nullable = false)
  private int evaluateCount;


  public SportsSkillEntity(MemberEntity member, SportsEntity sports, int score) {
    this.member = member;
    this.sports = sports;
    this.sportsScore = (long)score;
    this.evaluateCount = 1;
  }

  public void updateSportsSkill(int score){
    this.sportsScore += score;
    this.evaluateCount += 1;
  }
}
