package com.anonymous.usports.domain.record.controller;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

  private final RecordService recordService;

  @PostMapping("")
  public ResponseEntity<RecordRegister.Response> registerRecord(
      @RequestBody RecordRegister.Request request
      //FIXME ,@AuthenticationPrincipal MemberDTO member
  ) {
    Long memberId = 1L;
    RecordDto savedRecord = recordService.saveRecord(request, memberId);

    return ResponseEntity.ok(RecordRegister.Response.fromDto(savedRecord));
  }

}
