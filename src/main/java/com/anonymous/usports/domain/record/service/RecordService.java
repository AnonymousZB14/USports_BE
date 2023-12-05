package com.anonymous.usports.domain.record.service;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.global.type.RecordType;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RecordService {

  /**
   * 기록 게시글 등록
   */
  RecordDto saveRecord(RecordRegister.Request request, Long loginMemberId,
      List<MultipartFile> images);

  /**
   * 기록 게시글 리스트 출력
   */
  RecordListDto getRecordsPage(RecordType recordType, int page, Long loginMemberId);

  void updateRecord();

  /**
   * 기록 게시글 삭제
   */
  RecordDto deleteRecord(Long recordId, Long loginMemberId);

}
