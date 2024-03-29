package com.anonymous.usports.domain.participant.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.service.NotificationService;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PenaltyScheduler {

  private final ParticipantRepository participantRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;

  @Scheduled(cron = "0 0 3 * * *")
  @Transactional
  public void penaltySchedule() {
    log.info("[impose penalty on No Evaluation] Time : {}", LocalDateTime.now());
    int page = 0;

    int size = NumberConstant.PAGE_SIZE_SCHEDULING; // 100
    while (size == NumberConstant.PAGE_SIZE_SCHEDULING) {
      size = imposePenalty(page++);
      log.info("imposed size : {}", size);
    }
  }

  private int imposePenalty(int page) {
    LocalDate today = LocalDate.now(); //오늘
    LocalDateTime endDateTime = LocalDateTime.of(today.minusDays(3L), LocalTime.MAX);//3일전 끝
    LocalDateTime startDateTime = endDateTime.minusDays(1L);//3일전 시작

    Page<ParticipantEntity> findAll =
        participantRepository.findAllByEvaluationAtIsNullAndMeetingDateBetweenOrderByMeetingDate(
            startDateTime,
            endDateTime,
            PageRequest.of(page, NumberConstant.PAGE_SIZE_SCHEDULING));

    for (ParticipantEntity participantEntity : findAll.getContent()) {
      MemberEntity member = participantEntity.getMember();
      member.addPenaltyCount();
      memberRepository.save(member);
      //패널티 부여 알림
      notificationService.notify(member, NotificationCreateDto.imposePenalty(participantEntity.getRecruit()));
    }

    return findAll.getNumberOfElements();
  }

}
