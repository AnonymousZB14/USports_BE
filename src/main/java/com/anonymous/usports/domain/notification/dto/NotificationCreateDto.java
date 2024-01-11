package com.anonymous.usports.domain.notification.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.global.constant.NotificationConstant;
import com.anonymous.usports.global.type.NotificationSituation;
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
  private NotificationSituation notificationSituation;
  private Long targetEntityId;
  private String message;
  private String url;

  private static String getNotificationString(String title, String notificationConstant) {
    StringBuilder sb = new StringBuilder();
    return sb.append("[").append(title).append("] ").append(notificationConstant).toString();
  }

  /**
   * 알림 : [RecruitTitle] 모임에 참여 신청자가 있습니다.
   * 상황 : 해당 Recruit에 지원 신청 발생 시 HOST에 알림 - 내 모임에 지원 신청자가 있을 때
   */
  public static NotificationCreateDto joinRecruit(ParticipantEntity participantEntity, RecruitEntity recruitEntity){
    return NotificationCreateDto.builder()
        .type(NotificationType.NOTICE)
        .notificationSituation(NotificationSituation.JOIN_RECRUIT)
        .targetEntityId(participantEntity.getParticipantId())
        .message(getNotificationString(recruitEntity.getTitle(), NotificationConstant.PARTICIPATE_REQUEST))
        .url("/recruit/" + recruitEntity.getRecruitId())
        .build();
  }

  /**
   * 알림 : [RecruitTitle] 모임에 평가를 하지 않아서, 매너점수 -3의 패널티가 발생되었습니다. 다음부턴 꼭 한명 이상 평가를 해주세요!
   * 상황 : 패널티 부여 시 패널티 부여받는 당사자에게 알림
   */
  public static NotificationCreateDto imposePenalty(RecruitEntity recruit){
    return NotificationCreateDto.builder()
        .type(NotificationType.ALERT)
        .notificationSituation(NotificationSituation.IMPOSE_PENALTY)
        .targetEntityId(recruit.getRecruitId())
        .message(getNotificationString(recruit.getTitle(), NotificationConstant.IMPOSE_PENALTY))
        .url("/recruit/" + recruit.getRecruitId())
        .build();
  }

  /**
   * 알림 : [Recruit]모임 참여가 거절되었습니다.
   * 상황 : Recruit 신청 거절 시 Participant 에게 전송
   */
  public static NotificationCreateDto participateRefused(RecruitEntity recruit){
    return NotificationCreateDto.builder()
        .type(NotificationType.NOTICE)
        .notificationSituation(NotificationSituation.PARTICIPATE_REFUSED)
        .targetEntityId(recruit.getRecruitId())
        .message(getNotificationString(recruit.getTitle(), NotificationConstant.PARTICIPATE_REFUSED))
        .url("/recruit/" + recruit.getRecruitId())
        .build();
  }

  /**
   * 알림 : [RecruitTitle]모임 참여가 수락되었습니다.
   * 상황 : Recruit 신청 수락 시 Participant 에게 전송
   */
  public static NotificationCreateDto participateAccepted(RecruitEntity recruit){
    return NotificationCreateDto.builder()
        .type(NotificationType.NOTICE)
        .notificationSituation(NotificationSituation.PARTICIPATE_ACCEPTED)
        .targetEntityId(recruit.getRecruitId())
        .message(getNotificationString(recruit.getTitle(), NotificationConstant.PARTICIPATE_ACCEPTED))
        .url("/recruit/" + recruit.getRecruitId())
        .build();
  }

  /**
   * 알림 : [RecruitTitle]모임에서 평가를 받으셨습니다.
   * 상황 : 평가를 받았을 때, 피 평가자에게 알림
   */
  public static NotificationCreateDto evaluated(RecruitEntity recruit){
    return NotificationCreateDto.builder()
        .type(NotificationType.NOTICE)
        .notificationSituation(NotificationSituation.EVALUATED)
        .targetEntityId(recruit.getRecruitId())
        .message(getNotificationString(recruit.getTitle(), NotificationConstant.EVALUATED))
        .url("/mypage")
        .build();
  }



  public static NotificationEntity toEntity(NotificationCreateDto createDto, MemberEntity member){
    return NotificationEntity.builder()
        .member(member)
        .type(createDto.getType())
        .notificationSituation(createDto.getNotificationSituation())
        .targetEntityId(createDto.getTargetEntityId())
        .message(createDto.getMessage())
        .url(createDto.getUrl())
        .build();
  }
}
