package com.anonymous.usports.domain.notification.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  @ApiOperation("알림 리스트 보기")
  @GetMapping("/notifications")
  public @ResponseBody ResponseEntity<?> notificationList(
      @AuthenticationPrincipal MemberDto loginMember,
      HttpServletRequest httpServletRequest) {

    List<NotificationDto> notifications =
        notificationService.getNotifications(loginMember.getMemberId());

    //알림 리스트 조회 시 "안읽은 알림 없음 상태" 로 변경
    notificationService.setUnreadNotificationSession(httpServletRequest, false);

    return ResponseEntity.ok(notifications);
  }

}
