package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.global.type.NotificationType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
public class NotificationDto {

  private Long notificationId;

  private Long memberId;

  private NotificationType type;

  private Long targetEntityId;

  private String message;

  private LocalDateTime createdAt;

  private LocalDateTime readAt;

  public static NotificationDto fromEntity(NotificationEntity notification){
    return NotificationDto.builder()
        .notificationId(notification.getNotificationId())
        .memberId(notification.getMember().getMemberId())
        .type(notification.getType())
        .targetEntityId(notification.getTargetEntityId())
        .message(notification.getMessage())
        .createdAt(notification.getCreatedAt())
        .readAt(notification.getReadAt())
        .build();
  }
}
