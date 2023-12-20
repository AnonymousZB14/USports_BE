package com.anonymous.usports.domain.participant.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PenaltyScheduler {

  private final ParticipantRepository participantRepository;
  private final MemberRepository memberRepository;

  @Scheduled(cron = "0 0 3 * * *")
  @Transactional
  public void imposePenalty(){
    log.info("[impose penalty on No Evaluation] Time : {}", LocalDateTime.now());

    LocalDate today = LocalDate.now(); //오늘
    LocalDateTime yesterdayEnd = LocalDateTime.of(today.minusDays(3L), LocalTime.MAX);//3일전 끝
    LocalDateTime yesterdayStart = yesterdayEnd.minusDays(1L);//3일전 시작
    List<ParticipantEntity> findAll = participantRepository.findAllByEvaluationAtIsNullAndMeetingDateBetween(
        yesterdayStart, yesterdayEnd);

    for (ParticipantEntity participantEntity : findAll) {
      MemberEntity member = participantEntity.getMember();
      member.addPenaltyCount();
      memberRepository.save(member);
    }
  }
}
