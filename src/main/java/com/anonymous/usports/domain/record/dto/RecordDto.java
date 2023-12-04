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

  private String accountName;

  private String recordContent;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  private Long countComment;

  private List<String> imageAddressList;


  public static RecordDto fromEntity(RecordEntity recordEntity, List<RecordImageEntity> recordImageEntities) {
    return RecordDto.builder()
        .recordId(recordEntity.getRecordId())
        .memberId(recordEntity.getMember().getMemberId())
        .sportsId(recordEntity.getSports().getSportsId())
        .accountName(recordEntity.getMember().getAccountName())
        .recordContent(recordEntity.getRecordContent())
        .registeredAt(recordEntity.getRegisteredAt())
        .updatedAt(recordEntity.getUpdatedAt())
        .countComment(recordEntity.getCountComment())
        .imageAddressList(recordImageEntities.stream().map(x->x.getImageAddress()).collect(Collectors.toList()))
        .build();
  }
}
