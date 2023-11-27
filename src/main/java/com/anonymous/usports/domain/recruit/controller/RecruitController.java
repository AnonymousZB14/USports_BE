package com.anonymous.usports.domain.recruit.controller;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.recruit.dto.AddRecruit;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecruitController {

  private final RecruitService recruitService;


  @PostMapping("/recruit")
  public ResponseEntity<?> addRecruit(@RequestBody AddRecruit.Request request){
    //수정
    Long memberId = 0L;
    RecruitDto result = recruitService.addRecruit(request, memberId);

    return ResponseEntity.ok(AddRecruit.Response.fromDto(result));
  }


}
