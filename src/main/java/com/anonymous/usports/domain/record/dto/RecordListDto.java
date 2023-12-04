package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.record.entity.RecordEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordListDto {

  private int currentPage;
  private int pageSize;
  private int totalPages;
  private int totalElements;

  private List<RecordDto> list;


  public RecordListDto(Page<RecordEntity> recordEntityPage) {
    this.currentPage = recordEntityPage.getNumber() + 1;
    this.pageSize = recordEntityPage.getSize();
    this.totalPages = recordEntityPage.getTotalPages();
    this.totalElements = (int) recordEntityPage.getTotalElements();
    this.list = recordEntityPage.getContent().stream().map(RecordDto::fromEntity)
        .collect(Collectors.toList());
  }
}
