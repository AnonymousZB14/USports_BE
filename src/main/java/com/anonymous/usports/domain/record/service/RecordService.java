package com.anonymous.usports.domain.record.service;

import com.anonymous.usports.domain.record.dto.RecordDto;
import com.anonymous.usports.domain.record.dto.RecordRegisterDto;
import java.util.List;

public interface RecordService {

  RecordDto saveRecord(RecordRegisterDto.Request request, Long member);

  List<RecordDto> getRecordsList();

  void updateRecord();

  void deleteRecord();

}
