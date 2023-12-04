package com.anonymous.usports.domain.participant.dto;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ParticipantListDto {

  private int currentPage;//현재 페이지
  private int currentElements;//현재 페이지 데이터 개수
  private int pageSize; //한 페이지의 크기
  private int totalElement;//전체 데이터 개수
  private int totalPages;//전체 페이지 개수

  private List<ParticipantDto> list;

  public ParticipantListDto(Page<ParticipantEntity> page) {
    this.currentPage = page.getNumber() + 1;
    this.currentElements = page.getNumberOfElements();
    this.pageSize = page.getSize();
    this.totalElement = (int) page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.list = page.getContent().stream().map(ParticipantDto::fromEntity)
        .collect(Collectors.toList());
  }

}
