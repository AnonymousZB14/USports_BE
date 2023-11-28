package com.anonymous.usports.domain.participant.controller;

import com.anonymous.usports.domain.participant.dto.JoinRecruitResponse;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ParticipantController {

  private final ParticipantService participantService;

  @PostMapping("/recruit/{recruitId}/join")
  public ResponseEntity<?> joinRecruit(@PathVariable Long recruitId) {
    //FIXME : @AuthenticationPrincipal 에서 memberId 불러오기
    Long memberId = 0L;
    ParticipantDto result = participantService.joinRecruit(memberId, recruitId);
    return ResponseEntity.ok(new JoinRecruitResponse(result));
  }
}
