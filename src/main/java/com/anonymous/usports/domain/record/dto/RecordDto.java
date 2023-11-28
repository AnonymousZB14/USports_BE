package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.entity.RecordImageEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class RecordDto {

  private Long recordId;

  private Long memberId;

  private Long sportsId;

  private String recordContent;

  private LocalDateTime registerdAt;

  private LocalDateTime updatedAt;

  private List<String> images;

  public static RecordDto fromEntity(RecordEntity recordEntity,
      List<RecordImageEntity> recordImages) {
    List<String> imageAddresses = recordImages.stream()
        .map(RecordImageEntity::getImageAddress)
        .collect(Collectors.toList());

    return RecordDto.builder()
        .recordId(recordEntity.getRecordId())
        .memberId(recordEntity.getMember().getMemberId())
        .sportsId(recordEntity.getSports().getSportsId())
        .recordContent(recordEntity.getRecordContent())
        .registerdAt(recordEntity.getRegisterdAt())
        .updatedAt(recordEntity.getUpdatedAt())
        .images(imageAddresses)
        .build();
  }
}
