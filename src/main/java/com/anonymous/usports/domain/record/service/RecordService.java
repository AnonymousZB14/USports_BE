package com.anonymous.usports.domain.record.service;

import com.anonymous.usports.domain.record.dto.RecordDetail;
import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.dto.RecordUpdate;
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

  /**
   * 기록 게시글 수정 페이지
   */
  RecordDto getRecordUpdatePage(Long recordId, Long loginMemberId);

  /**
   * 기록 게시글 수정
   */
  RecordDto updateRecord(Long recordId, RecordUpdate.Request request, Long loginMemberId);

  /**
   * 기록 게시글 삭제
   */
  RecordDto deleteRecord(Long recordId, Long loginMemberId);

  /**
   * 기록 게시글 상세 페이지
   */
  RecordDetail getRecordDetail(Long recordId, int page, Long loginMemberId);
}
