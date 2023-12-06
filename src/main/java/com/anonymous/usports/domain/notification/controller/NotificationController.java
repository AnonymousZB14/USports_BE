package com.anonymous.usports.domain.notification.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


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

  @ApiOperation("알림 읽기")
  @GetMapping("/notifications/{notificationId}")
  public ResponseEntity<?> getNotification(@PathVariable Long notificationId) {

    return ResponseEntity.ok(null);
  }

}
