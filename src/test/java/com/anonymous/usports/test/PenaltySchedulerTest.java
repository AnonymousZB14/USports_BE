package com.anonymous.usports.test;

import com.anonymous.usports.domain.participant.service.PenaltyScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class PenaltySchedulerTest {

  @Autowired
  private PenaltyScheduler penaltyScheduler;

  @Test
  void test(){

    penaltyScheduler.penaltySchedule();

  }
}
