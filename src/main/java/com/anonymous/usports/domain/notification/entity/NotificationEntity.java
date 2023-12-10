package com.anonymous.usports.domain.notification.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "notification")
public class NotificationEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notificationId", nullable = false)
  private Long notificationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type; //알림 타입 (ALERT, RECORD, NOTICE)

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false)
  private NotificationEntityType entityType; //어떤 엔티티에 대한 알림인지 타입

  @Column(name = "target_entity_id", nullable = false)
  private Long targetEntityId; //type에서 선택된 타겟 엔티티의 id(PK)값

  @Column(name = "message")
  private String message;

  @Column(name = "url")
  private String url;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  public void readNow(){
    this.readAt = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    NotificationEntity that = (NotificationEntity) object;
    return Objects.equals(notificationId, that.notificationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationId);
  }
}
