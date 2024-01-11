package com.anonymous.usports.domain.notification.service.impl;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.notification.repository.EmitterRepository;
import com.anonymous.usports.domain.notification.repository.NotificationRepository;
import com.anonymous.usports.domain.notification.service.NotificationService;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
public class NotificationServiceImpl implements NotificationService {

  private static final Long DEFAULT_TIMEOUT = 1000 * 60 * 60L;
  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;
  private final MemberRepository memberRepository;

  @Override
  public List<NotificationDto> getNotifications(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    List<NotificationEntity> notificationList =
        notificationRepository.findByMemberOrderByCreatedAtDesc(member);

    for (NotificationEntity n : notificationList) {
      n.readNow();
    }
    List<NotificationEntity> saved = notificationRepository.saveAll(
        notificationList);

    return saved.stream()
        .map(NotificationDto::fromEntity)
        .collect(Collectors.toList());
  }


  /**
   * 클라이언트가 구독을 위해 호출하는 메서드
   */
  @Override
  public SseEmitter subscribe(Long memberId) {
    //이미터 생성
    SseEmitter emitter = this.createEmitter(memberId);

    this.sendToClient(memberId, "EventStream Created. [memberId=" + memberId + "]");
    return emitter;
  }

  @Override
  public NotificationDto notify(MemberEntity member, NotificationCreateDto notificationCreateDto) {
    NotificationEntity saved =
        notificationRepository.save(NotificationCreateDto.toEntity(notificationCreateDto, member));

    this.sendToClient(member.getMemberId(), notificationCreateDto.getMessage());

    return NotificationDto.fromEntity(saved);
  }

  @Override
  public boolean checkUnreadNotification(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    return notificationRepository.existsByMemberAndReadAtIsNull(member);
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
