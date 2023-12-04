package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.entity.RecordImageEntity;
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
public class RecordImageDto {

  private Long imageId;
  private Long recordId;
  private String imageAddress;

  public static RecordImageEntity toEntity(RecordEntity recordEntity, String imageAddress) {
    return RecordImageEntity.builder()
        .record(recordEntity)
        .imageAddress(imageAddress)
        .build();
  }

  public static RecordImageDto fromEntity(RecordImageDto recordImageDto) {
    return RecordImageDto.builder()
        .imageId(recordImageDto.getImageId())
        .recordId(recordImageDto.getRecordId())
        .imageAddress(recordImageDto.getImageAddress())
        .build();
  }
}
