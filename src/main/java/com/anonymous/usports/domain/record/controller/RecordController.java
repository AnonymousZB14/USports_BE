package com.anonymous.usports.domain.record.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.record.dto.RecordDelete;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.dto.RecordRegister.Response;
import com.anonymous.usports.domain.record.dto.RecordUpdate;
import com.anonymous.usports.domain.record.service.RecordService;
import com.anonymous.usports.global.type.RecordType;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class RecordController {

  private final RecordService recordService;

  @ApiOperation("기록 작성 페이지")
  @GetMapping("/record")
  public ResponseEntity<?> registerRecordPage() {
    //TODO
    return ResponseEntity.ok(null);
  }

  @ApiOperation("팔로우, 추천 기록 내용 보기")
  @GetMapping("/home")
  public ResponseEntity<RecordListDto> getRecordList(
      @RequestParam(value = "type", defaultValue = "RECOMMENDATION") RecordType recordType,
      @RequestParam(value = "page",defaultValue = "1") int page,
      @AuthenticationPrincipal MemberDto loginMember) {
    RecordListDto records = recordService.getRecordsPage(recordType, page,
        loginMember.getMemberId());
    return ResponseEntity.ok(records);
  }


  @ApiOperation("기록 등록하기")
  @PostMapping("/record")
  public ResponseEntity<RecordRegister.Response> registerRecord(
      @RequestPart("request") RecordRegister.Request request,
      @RequestPart("images") List<MultipartFile> images,
      @AuthenticationPrincipal MemberDto loginMember
  ) {

    RecordDto savedRecord = recordService.saveRecord(request, loginMember.getMemberId(), images);

    return ResponseEntity.ok(new Response(savedRecord));
  }

  @ApiOperation("기록 삭제하기")
  @DeleteMapping("/record/{recordId}")
  public ResponseEntity<RecordDelete.Response> deleteRecord(@PathVariable Long recordId,
      @AuthenticationPrincipal MemberDto loginMember) {
    RecordDto recordDto = recordService.deleteRecord(recordId, loginMember.getMemberId());
    return ResponseEntity.ok(new RecordDelete.Response(recordDto));
  }

  @ApiOperation("기록 수정하기")
  @PutMapping("/record/{recordId}")
  public ResponseEntity<RecordUpdate.Response> updateRecord(@PathVariable Long recordId,
      @RequestBody RecordUpdate.Request request,
      @AuthenticationPrincipal MemberDto loginMember) {
    RecordDto updateRecord = recordService.updateRecord(recordId, request, loginMember.getMemberId());
    return ResponseEntity.ok(new RecordUpdate.Response(updateRecord));
  }

}
