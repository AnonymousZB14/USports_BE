package com.anonymous.usports.domain.notification.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "알림(Notification)")
@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  @ApiOperation("알림 리스트 보기")
  @GetMapping("/notifications")
  public ResponseEntity<List<NotificationDto>> notificationList(
      @AuthenticationPrincipal MemberDto loginMember) {

    List<NotificationDto> notifications =
        notificationService.getNotifications(loginMember.getMemberId());

    return ResponseEntity.ok(notifications);
  }

  @ApiOperation("안읽은 알림 존재 여부")
  @GetMapping("/notification/unread")
  public ResponseEntity<Map<String, Boolean>> unreadNotificationExists(
      @AuthenticationPrincipal MemberDto loginMember) {

    boolean result = notificationService.checkUnreadNotification(loginMember.getMemberId());
    return ResponseEntity.ok(Map.of("unreadNotificationExists", result));
  }

}
