package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.constant.NotificationConstant;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class NotificationCreateDto {

  private NotificationType type;
  private NotificationEntityType entityType;
  private Long targetEntityId;
  private String message;
  private String url;

  private String getNotificationString(String header, String notificationConstant) {
    StringBuilder sb = new StringBuilder();
    return sb.append("[").append(header).append("] ").append(notificationConstant).toString();
  }

  /**
   * Join Recruit 에서 사용
   */
  public NotificationCreateDto(ParticipantEntity participantEntity,RecruitEntity recruitEntity) {
    this.type = NotificationType.NOTICE;
    this.entityType = NotificationEntityType.PARTICIPANT;
    this.targetEntityId = participantEntity.getParticipantId();
    this.message = this.getNotificationString(recruitEntity.getTitle(),
        NotificationConstant.PARTICIPATE_REQUEST);
  }

  /**
   * 패널티 부여 시
   */
  public NotificationCreateDto(RecruitEntity recruit) {
    this.type = NotificationType.ALERT;
    this.entityType = NotificationEntityType.RECRUIT;
    this.targetEntityId = recruit.getRecruitId();
    this.message = this.getNotificationString(recruit.getTitle(), NotificationConstant.IMPOSE_PENALTY);
    this.url = "/recruit/"+ recruit.getRecruitId();
  }

  public static NotificationEntity toEntity(NotificationCreateDto createDto, MemberEntity member){
    return NotificationEntity.builder()
        .member(member)
        .type(createDto.getType())
        .entityType(createDto.getEntityType())
        .targetEntityId(createDto.getTargetEntityId())
        .message(createDto.getMessage())
        .url(createDto.getUrl())
        .build();
  }
}
