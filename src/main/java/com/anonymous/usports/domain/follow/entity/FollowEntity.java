package com.anonymous.usports.domain.follow.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.FollowStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity(name = "follow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "follow_id", nullable = false)
  private Long followId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_member_id", nullable = false)
  private MemberEntity fromMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_member_id", nullable = false)
  private MemberEntity toMember;

  @Enumerated(EnumType.STRING)
  @Column(name = "follow_status")
  private FollowStatus followStatus;

  @Column(name="follow_date")
  private LocalDateTime followDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FollowEntity follow = (FollowEntity) o;
    return Objects.equals(followId, follow.followId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(followId);
  }
}
