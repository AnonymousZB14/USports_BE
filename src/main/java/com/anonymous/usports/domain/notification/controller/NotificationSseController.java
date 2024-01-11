package com.anonymous.usports.domain.notification.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Api(tags = "알림 SSE(Notification SSE 관련)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationSseController {

  private final NotificationService notificationService;
  private final MemberRepository memberRepository;

  @ApiOperation(value = "구독을 시작하기 위한 메서드")
  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@AuthenticationPrincipal MemberDto loginMember) {

    return notificationService.subscribe(loginMember.getMemberId());
  }

  @ApiOperation(value = "구독을 시작하기 위한 메서드")
  @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
  public SseEmitter subscribeTest(@PathVariable Long id) {

    return notificationService.subscribe(id);
  }

  /**
   * !!! 테스트용 실제로는 알림을 보내는 곳에서 notificatoinService.notify() 호출
   * FIXME : 테스트 끝나면 삭제하기
   */
  @ApiOperation(value = "테스트를 위한 api", notes = "이후에 실제 사용시에는 아예 삭제 될 메서드이다.")
  @GetMapping(value = "/send/{id}")
  public void sendData(@PathVariable Long id, @RequestParam String d) {
    MemberEntity member = memberRepository.findById(id).get();
    NotificationCreateDto req = NotificationCreateDto.builder()
        .type(NotificationType.NOTICE)
        .entityType(NotificationEntityType.PARTICIPANT)
        .targetEntityId(5L)
        .message(d)
        .build();
    notificationService.notify(member, req);

  }


}
