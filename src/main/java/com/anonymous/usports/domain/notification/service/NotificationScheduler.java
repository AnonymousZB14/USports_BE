package com.anonymous.usports.domain.notification.service;

import com.anonymous.usports.domain.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationScheduler {

  private final NotificationRepository notificationRepository;

  @Scheduled(cron = "0 0 2 * * *")//매일 오전 2시
  @Transactional
  public void deleteNotifications() {
    log.info("[notification delete Scheduler start] Time: {}", LocalDateTime.now());

    Integer count =
        notificationRepository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusDays(30L));

    log.info("[delete Complete] count={} Time: {}", count, LocalDateTime.now());
  }
}
