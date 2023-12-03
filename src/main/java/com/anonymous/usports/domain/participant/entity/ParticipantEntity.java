package com.anonymous.usports.domain.participant.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.type.ParticipantStatus;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "participant")
public class ParticipantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "participant_id", nullable = false)
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
  private LocalDateTime confirmedAt; //모집 확인 받은 시간

  @Enumerated(EnumType.STRING)
  @Column(name = "participant_status", nullable = false)
  private ParticipantStatus status;

  @Column(name = "evaluation_at")
  private LocalDateTime evaluationAt; //타인 평가 일시

  public ParticipantEntity(MemberEntity member, RecruitEntity recruit) {
    this.member = member;
    this.recruit = recruit;
    this.status = ParticipantStatus.ING;
  }

  public void confirm() {
    this.confirmedAt = LocalDateTime.now();
    this.status = ParticipantStatus.ACCEPTED;
  }

  public void refuse(){
    this.confirmedAt = LocalDateTime.now();
    this.status = ParticipantStatus.REFUSED;
  }

  public void evaluation() {
    this.evaluationAt = LocalDateTime.now();
  }
}
