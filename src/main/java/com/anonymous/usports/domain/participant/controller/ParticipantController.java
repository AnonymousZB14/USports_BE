package com.anonymous.usports.domain.participant.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipateCancel;
import com.anonymous.usports.domain.participant.dto.ParticipateResponse;
import com.anonymous.usports.domain.participant.service.ParticipantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "모집 신청(Participant)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ParticipantController {

  private final ParticipantService participantService;

  @ApiOperation(value = "운동 모집 글 지원자 조회")
  @GetMapping("/recruit/{recruitId}/applicants")
  public ResponseEntity<ParticipantListDto> getApplicants(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {

    ParticipantListDto result =
        participantService.getParticipants(recruitId, loginMember.getMemberId());

    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "운동 모집 게시글에 참여 신청 넣기", notes = "Participant entity 생성")
  @PostMapping("/recruit/{recruitId}/join")
  public ResponseEntity<ParticipateResponse> joinRecruit(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {

    ParticipateResponse result = participantService.joinRecruit(
        loginMember.getMemberId(), recruitId);

    return ResponseEntity.ok(result);
  }

  @ApiOperation("지원자 참여 요청 수락 / 거절")
  @PostMapping("/recruit/{recruitId}/manage")
  public ResponseEntity<ParticipantManage.Response> manageJoinRecruit(@PathVariable Long recruitId,
      @RequestBody ParticipantManage.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {

    ParticipantManage.Response result =
        participantService.manageJoinRecruit(request, recruitId,
            loginMember.getMemberId());

    return ResponseEntity.ok(result);
  }


  @ApiOperation("운동 모집 신청 취소")
  @PutMapping("/recruit/{recruitId}/cancel")
  public ResponseEntity<ParticipateCancel> cancelJoinRecruit(@PathVariable Long recruitId,
      @AuthenticationPrincipal MemberDto loginMember) {
    ParticipateCancel result =
        participantService.cancelJoinRecruit(recruitId, loginMember.getMemberId());

    return ResponseEntity.ok(result);
  }

}
