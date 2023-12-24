package com.anonymous.usports.domain.recruit.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDateTime;
import java.util.Objects;
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
  @Column(name = "recruit_id", nullable = false)
  private Long recruitId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sports_id", nullable = false)
  private SportsEntity sports;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Column(name = "chatroom_id")
  private Long chatRoomId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "place_name", nullable = false)
  private String placeName;

  @Column(name = "region", nullable = false)
  private String region;

  @Column(name = "street_name_addr")
  private String streetNameAddr; // 도로명 주소

  @Column(name = "street_number_addr")
  private String streetNumberAddr; // 지번 주소

  @Column(name = "post_code")
  private String postCode;

  @Column(name = "lat", nullable = false)
  private String lat;

  @Column(name = "lnt", nullable = false)
  private String lnt;

  @Column(name = "cost", nullable = false)
  private int cost;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @Column(name = "current_count", nullable = false)
  private int currentCount;

  @Column(name = "recruit_count", nullable = false)
  private int recruitCount;

  @Column(name = "meeting_date", nullable = false)
  private LocalDateTime meetingDate;

  @Enumerated(EnumType.STRING)
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

  public void participantAdded() {
    this.currentCount = this.currentCount + 1;
    if (this.currentCount == this.recruitCount) {
      this.recruitStatus = RecruitStatus.END;
    } else if ((double) this.currentCount / this.recruitCount >= 0.7) {
      this.recruitStatus = RecruitStatus.ALMOST_END;
    }
  }

  public void participantCanceled() {
    if (this.currentCount == 0) {
      return;
    }
    this.currentCount = this.currentCount - 1;
    if (this.recruitStatus == RecruitStatus.ALMOST_END
        && (double) this.currentCount / this.recruitCount <= 0.7) {
      this.recruitStatus = RecruitStatus.RECRUITING;
    }
  }

  public void statusToEnd() {
    this.recruitStatus = RecruitStatus.END;
  }

  public void statusToRecruiting() {
    this.recruitStatus = RecruitStatus.RECRUITING;
  }

  public void statusToAlmostFinished() {
    this.recruitStatus = RecruitStatus.ALMOST_END;
  }


  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    RecruitEntity that = (RecruitEntity) object;
    return Objects.equals(recruitId, that.recruitId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recruitId);
  }
}
