package com.anonymous.usports.domain.evaluation.controller;

import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Response;
import com.anonymous.usports.domain.evaluation.service.EvaluationService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "평가(Evaluation)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class EvaluationController {

  private final EvaluationService evaluationService;

  @ApiOperation("회원간에 평가를 진행한다.")
  @PostMapping("/evaluation")
  public ResponseEntity<EvaluationRegister.Response> registerEvaluation(@RequestBody EvaluationRegister.Request request,
      @AuthenticationPrincipal MemberDto loginMember){
    EvaluationRegister.Response response = evaluationService.registerEvaluation(request, loginMember.getMemberId());
    return ResponseEntity.ok(response);
  }
}
