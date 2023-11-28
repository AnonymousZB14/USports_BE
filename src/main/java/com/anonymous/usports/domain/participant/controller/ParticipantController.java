package com.anonymous.usports.domain.participant.controller;

import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantResponse;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ParticipantController {

  private final ParticipantService participantService;

  @ApiOperation(value = "운동 모집 글 지원자 조회", notes = "page는 1부터 시작한다.")
  @GetMapping("/recruit/{recruitId}/applicants")
  public ResponseEntity<?> getApplicants(@PathVariable Long recruitId,
      @RequestParam(name = "page", defaultValue = "1") int page) {
    Long memberId = 0L;//FIXME : @AuthenticationPrincipal 에서 memberId 불러오기
    ParticipantListDto result = participantService.getParticipants(recruitId, page, memberId);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "운동 모집 게시글에 참여 신청 넣기", notes = "Participant entity 생성")
  @PostMapping("/recruit/{recruitId}/join")
  public ResponseEntity<?> joinRecruit(@PathVariable Long recruitId) {

    Long memberId = 0L;//FIXME : @AuthenticationPrincipal 에서 memberId 불러오기

    ParticipantDto result = participantService.joinRecruit(memberId, recruitId);

    return ResponseEntity.ok(new ParticipantResponse(result));
  }

  @ApiOperation("지원자 참여 요청 수락 / 거절")
  @PostMapping("/recruit/{recruitId}/manage")
  public ResponseEntity<?> manageJoinRecruit(@PathVariable Long recruitId,
      @RequestBody ParticipantManage.Request request) {

    Long memberId = 0L;//FIXME : @AuthenticationPrincipal 에서 memberId 불러오기

    ParticipantManage.Response result = participantService.manageJoinRecruit(request, recruitId,
        memberId);

    return ResponseEntity.ok(result);
  }
}
