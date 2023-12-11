package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
import java.time.LocalDateTime;
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
public class NotificationDto {

  private Long notificationId;

  private Long memberId;

  private NotificationType type;

  private NotificationEntityType entityType;

  private Long targetEntityId;

  private String message;

  private String url;

  private LocalDateTime createdAt;

  private LocalDateTime readAt;

  public static NotificationDto fromEntity(NotificationEntity notification){
    return NotificationDto.builder()
        .notificationId(notification.getNotificationId())
        .memberId(notification.getMember().getMemberId())
        .type(notification.getType())
        .entityType(notification.getEntityType())
        .targetEntityId(notification.getTargetEntityId())
        .message(notification.getMessage())
        .url(notification.getUrl())
        .createdAt(notification.getCreatedAt())
        .readAt(notification.getReadAt())
        .build();
  }
}
