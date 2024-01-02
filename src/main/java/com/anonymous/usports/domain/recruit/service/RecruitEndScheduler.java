package com.anonymous.usports.domain.recruit.service;

import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.type.RecruitStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecruitEndScheduler {

  private final RecruitRepository recruitRepository;

  @Scheduled(cron = "0 15 * * * *") //매시 15분에 실행
  @Transactional
  public void penaltySchedule() {
    log.info("[Change status to END on finished Recruit] Time : {}", LocalDateTime.now());

    List<RecruitEntity> recruitingList =
        recruitRepository.findAllByRecruitStatusAndMeetingDateIsAfter(
            RecruitStatus.RECRUITING, LocalDateTime.now());

    for (RecruitEntity recruitEntity : recruitingList) {
      recruitEntity.setRecruitStatus(RecruitStatus.END);
    }
    recruitRepository.saveAll(recruitingList);

    List<RecruitEntity> almostEndList =
        recruitRepository.findAllByRecruitStatusAndMeetingDateIsAfter(
            RecruitStatus.ALMOST_END, LocalDateTime.now());

    for (RecruitEntity recruitEntity : almostEndList) {
      recruitEntity.setRecruitStatus(RecruitStatus.END);
    }
    recruitRepository.saveAll(almostEndList);
    log.info("[Change status to END on finished Recruit] : finish , amount : {}",
        recruitingList.size() + almostEndList.size());
  }
}
