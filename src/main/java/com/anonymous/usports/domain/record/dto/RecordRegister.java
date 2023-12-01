package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class RecordRegister {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    private Long sportsId;
    private String content;

    public static RecordEntity toEntity(RecordRegister.Request request, MemberEntity member,
        SportsEntity sports) {
      return RecordEntity.builder()
          .member(member)
          .sports(sports)
          .recordContent(request.getContent())
          .build();
    }
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
      this.message = ResponseConstant.CREATE_RECORD;;
    }
  }

}
