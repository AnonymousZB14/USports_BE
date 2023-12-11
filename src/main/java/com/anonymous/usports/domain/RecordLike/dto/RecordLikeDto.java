package com.anonymous.usports.domain.RecordLike.dto;

import com.anonymous.usports.domain.RecordLike.entity.RecordLikeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordLikeDto {

  private Long recordLikeId;
  private Long memberId;
  private Long recordId;
  private String message;

  public static RecordLikeDto fromEntity(RecordLikeEntity recordLikeEntity) {
    return RecordLikeDto.builder()
        .recordLikeId(recordLikeEntity.getRecordLikeId())
        .memberId(recordLikeEntity.getMember().getMemberId())
        .recordId(recordLikeEntity.getRecord().getRecordId())
        .build();
  }
}
