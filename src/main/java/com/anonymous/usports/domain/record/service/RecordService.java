package com.anonymous.usports.domain.record.service;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.entity.RecordImageEntity;
import com.anonymous.usports.global.type.RecordType;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RecordService {

  RecordDto saveRecord(RecordRegister.Request request, Long member, List<MultipartFile> images);

  RecordListDto getRecordsPage(RecordType recordType, int page, Long memberId);

  void updateRecord();

  void deleteRecord();

}
