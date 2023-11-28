package com.anonymous.usports.domain.participant.entity;

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

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "participant")
public class ParticipantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long participantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruit_id", nullable = false)
  private RecruitEntity recruit;

  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt; //모집 수락 받은 시간

  @Column(name = "evaluation_at")
  private LocalDateTime evaluationAt; //타인 평가 일시

  public ParticipantEntity(MemberEntity member, RecruitEntity recruit) {
    this.member = member;
    this.recruit = recruit;
    this.registeredAt = LocalDateTime.now();
  }

  public void confirmedNow(){
    this.confirmedAt = LocalDateTime.now();
  }

  public void evaluationNow(){
    this.evaluationAt = LocalDateTime.now();
  }
}
