package com.anonymous.usports.domain.recruit.controller;

import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecruitController {

  private final RecruitService recruitService;

  @ApiOperation("운동 모집 게시글 등록 페이지")
  @GetMapping("/recruit")
  public ResponseEntity<?> registerRecruitPage(){

      return ResponseEntity.ok(null);
  }

  @ApiOperation("운동 모집 게시글 등록하기")
  @PostMapping("/recruit")
  public ResponseEntity<?> registerRecruit(@RequestBody RecruitRegister.Request request){
    //수정
    Long memberId = 0L;
    RecruitDto result = recruitService.addRecruit(request, memberId);

    return ResponseEntity.ok(RecruitRegister.Response.fromDto(result));
  }

  @ApiOperation("운동 모집 게시글 한 건 조회하기")
  @GetMapping("/recruit/{recruitId}")
  public ResponseEntity<?> getRecruit(@PathVariable Long recruitId){
    RecruitDto result = recruitService.getRecruit(recruitId);
    return ResponseEntity.ok(result);
  }

}
