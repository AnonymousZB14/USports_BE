package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RecordUpdate {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    private Long sportsId;
    private String recordContent;
    private List<String> removeImageAddress;

  }


  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  public static class Response {

    private Long recordId;
    private String message;

    public Response(RecordDto recordDto) {
      this.recordId = recordDto.getRecordId();
      this.message = ResponseConstant.UPDATE_RECORD;
    }
  }

}
