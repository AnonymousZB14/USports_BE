package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
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
public class NotificationCreateDto {
  private NotificationType type;
  private NotificationEntityType entityType;
  private Long targetEntityId;
  private String event;
}
