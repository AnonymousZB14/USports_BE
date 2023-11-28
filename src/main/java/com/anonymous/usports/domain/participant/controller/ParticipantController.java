package com.anonymous.usports.domain.participant.controller;

import com.anonymous.usports.domain.participant.dto.JoinRecruitManage;
import com.anonymous.usports.domain.participant.dto.JoinRecruitResponse;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ParticipantController {

  private final ParticipantService participantService;

  @ApiOperation(value = "운동 모집 게시글에 참여 신청 넣기", notes = "Participant entity 생성")
  @PostMapping("/recruit/{recruitId}/join")
  public ResponseEntity<?> joinRecruit(@PathVariable Long recruitId) {

    Long memberId = 0L;//FIXME : @AuthenticationPrincipal 에서 memberId 불러오기

    ParticipantDto result = participantService.joinRecruit(memberId, recruitId);

    return ResponseEntity.ok(new JoinRecruitResponse(result));
  }

  @ApiOperation("지원자 참여 요청 수락 / 거절")
  @PostMapping("/recruit/{recruitId}/manage")
  public ResponseEntity<?> manageJoinRecruit(@PathVariable Long recruitId,
      @RequestBody JoinRecruitManage.Request request) {

    Long memberId = 0L;//FIXME : @AuthenticationPrincipal 에서 memberId 불러오기

    JoinRecruitManage.Response result = participantService.manageJoinRecruit(request, recruitId, memberId);

    return ResponseEntity.ok(result);
  }
}
