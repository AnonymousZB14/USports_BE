package com.anonymous.usports.domain.notification.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.notification.repository.EmitterRepository;
import com.anonymous.usports.domain.notification.repository.NotificationRepository;
import com.anonymous.usports.global.type.NotificationType;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Service
public class NotificationService {

  private static final Long DEFAULT_TIMEOUT = 1000 * 60 * 60L;
  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  /**
   * 클라이언트가 구독을 위해 호출하는 메서드
   */
  public SseEmitter subscribe(Long memberId) {
    //이미터 생성
    SseEmitter emitter = this.createEmitter(memberId);

    this.sendToClient(memberId, "EventStream Created. [memberId=" + memberId +"]");
    return emitter;
  }

  /**
   * 서버의 이벤트를 클라이언트에게 보내는 메서드
   * - 이 메서드는 실제 알림을 보내는 곳에서 사용한다.
   */
  public NotificationDto notify(MemberEntity member, NotificationType type, Long targetEntityId, Object event){
    NotificationEntity notification = NotificationEntity.builder()
        .member(member)
        .type(type)
        .targetEntityId(targetEntityId)
        .message(event.toString())
        .build();
    NotificationEntity saved = notificationRepository.save(notification);

    this.sendToClient(member.getMemberId(), event);

    return NotificationDto.fromEntity(saved);
  }

  /**
   * 클라이언트에게 데이터 전송
   */
  private void sendToClient(Long id, Object data) {
    SseEmitter emitter = emitterRepository.get(id);
    if (emitter != null) {
      try {
        //sse 이벤트 생성
        SseEventBuilder sse = SseEmitter.event()
            .id(String.valueOf(id))
            .name("sse")
            .data(data);
        emitter.send(sse);
      } catch (IOException e) {
        emitterRepository.deleteById(id);
        emitter.completeWithError(e);
      }
    }
  }

  private SseEmitter createEmitter(Long id) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitterRepository.save(id, emitter);

    //모든 데이터가 성공적으로 전송 된 상태 -> emitter 삭제
    emitter.onCompletion(() -> emitterRepository.deleteById(id));
    //타임아웃 되었을 때(지정 된 시간동안 아무 데이터도 전송되지 않았을 때) -> emitter 삭제
    emitter.onTimeout(() -> emitterRepository.deleteById(id));

    return emitter;

  }

}
