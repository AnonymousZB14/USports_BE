package com.anonymous.usports.domain.record.controller;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.service.RecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

  private final RecordService recordService;

  @PostMapping("")
  public ResponseEntity<RecordRegister.Response> registerRecord(
      @RequestPart("request") RecordRegister.Request request, @RequestPart("images")List<MultipartFile> images
      //FIXME ,@AuthenticationPrincipal MemberDTO member
  ) {
    Long memberId = 1L;
    request.setSportsId(1L);

    RecordDto savedRecord = recordService.saveRecord(request, memberId, images);

    return ResponseEntity.ok(RecordRegister.Response.fromDto(savedRecord));
  }

}
