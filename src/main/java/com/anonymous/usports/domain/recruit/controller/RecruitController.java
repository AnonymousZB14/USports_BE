package com.anonymous.usports.domain.recruit.controller;

import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecruitController {

  private final RecruitService recruitService;

  @ApiOperation("운동 모집 게시글 등록하기")
  @PostMapping("/recruit")
  public ResponseEntity<?> registerRecruit(@RequestBody RecruitRegister.Request request){
    //수정
    Long memberId = 0L;
    RecruitDto result = recruitService.addRecruit(request, memberId);

    return ResponseEntity.ok(RecruitRegister.Response.fromDto(result));
  }


}
