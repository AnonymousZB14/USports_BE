package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
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
  private static final String PREFIX = "http://localhost:8080";

  private NotificationType type;
  private NotificationEntityType entityType;
  private Long targetEntityId;
  private String event;
  private String url;

  public static NotificationEntity toEntity(NotificationCreateDto createDto, MemberEntity member){
    return NotificationEntity.builder()
        .member(member)
        .type(createDto.getType())
        .entityType(createDto.getEntityType())
        .targetEntityId(createDto.getTargetEntityId())
        .message(createDto.getEvent())
        .url(PREFIX + createDto.getUrl())
        .build();
  }
}
