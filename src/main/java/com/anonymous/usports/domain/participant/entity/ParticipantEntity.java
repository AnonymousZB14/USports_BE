package com.anonymous.usports.domain.participant.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

  @CreatedDate
  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt; //모집 수락 받은 시간

  @Column(name = "evaluation_at")
  private LocalDateTime evaluationAt; //타인 평가 일시

  public ParticipantEntity(MemberEntity member, RecruitEntity recruit) {
    this.member = member;
    this.recruit = recruit;
  }

  public void confirm() {
    this.confirmedAt = LocalDateTime.now();
  }

  public void evaluation() {
    this.evaluationAt = LocalDateTime.now();
  }
}
