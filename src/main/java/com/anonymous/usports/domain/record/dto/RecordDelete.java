package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RecordDelete {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {

    private Long recordId;
    private String message;

    public Response(RecordDto recordDto) {
      this.recordId = recordDto.getRecordId();
      this.message = ResponseConstant.DELETE_RECORD;

    }
  }

}
