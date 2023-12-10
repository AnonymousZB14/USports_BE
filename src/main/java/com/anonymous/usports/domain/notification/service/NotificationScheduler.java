package com.anonymous.usports.domain.notification.service;

import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    int size = 100;
    while(size > 0){
      List<NotificationEntity> list =
          notificationRepository.findTop100ByCreatedAtBefore(LocalDateTime.now().minusDays(30L));
      size = list.size();
      notificationRepository.deleteAll(list);
    }

    log.info("[delete Complete] Time: {}",  LocalDateTime.now());
  }
}
