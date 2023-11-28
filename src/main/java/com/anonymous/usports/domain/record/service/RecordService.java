package com.anonymous.usports.domain.record.service;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordRegister;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RecordService {

  RecordDto saveRecord(RecordRegister.Request request, Long member, List<MultipartFile> images);

  List<RecordDto> getRecordsList();

  void updateRecord();

  void deleteRecord();

}
