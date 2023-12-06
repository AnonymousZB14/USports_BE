package com.anonymous.usports.domain.notification.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.service.NotificationService;
import com.anonymous.usports.global.type.NotificationType;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SseController {

  private final NotificationService notificationService;
  private final MemberRepository memberRepository;

  @ApiOperation(value = "구독을 시작하기 위한 메서드", notes = "테스트를 위해 id를 PathVariable로 설정했지만, 실제로는 loginMemberId를 사용")
  @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@PathVariable Long id,
      @AuthenticationPrincipal MemberDto loginMember) {
    //return notificationService.subscribe(loginMember.getMemberId());
    return notificationService.subscribe(id);
  }

  /**
   * !!! 테스트용
   */
  @ApiOperation(value = "테스트를 위한 api", notes = "이후에 실제 사용시에는 아예 삭제 될 메서드이다.")
  @GetMapping(value = "/send/{id}")
  public void sendDate(@PathVariable Long id, @RequestParam String d) {
    MemberEntity member = memberRepository.findById(id).get();
    notificationService.notify(member, NotificationType.PARTICIPANT, 5L, d);
  }

}
