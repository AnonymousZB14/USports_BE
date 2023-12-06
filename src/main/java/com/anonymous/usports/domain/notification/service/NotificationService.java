package com.anonymous.usports.domain.notification.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  /**
   * 유저가 읽지 않은 알림 리스트 반환
   */
  List<NotificationDto> getNotifications(Long memberId);

  /**
   * 클라이언트가 구독을 위해 호출하는 메서드
   */
  SseEmitter subscribe(Long memberId);

  /**
   * 서버의 이벤트를 클라이언트에게 보내는 메서드 - 이 메서드는 실제 알림을 보내는 곳에서 사용한다.
   */
  NotificationDto notify(MemberEntity member, NotificationCreateDto notificationCreateDto);


}
