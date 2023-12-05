package com.anonymous.usports.domain.recruit.dto;

import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitSearchListDto {

  private int currentPage;//현재 페이지
  private int currentElements;//현재 페이지 데이터 개수
  private int pageSize; //한 페이지의 크기
  private int totalElement;//전체 데이터 개수
  private int totalPages;//전체 페이지 개수

  List<RecruitDto> list;

  public RecruitSearchListDto(Page<RecruitEntity> page) {
    this.currentPage = page.getNumber() + 1;
    this.currentElements = page.getNumberOfElements();
    this.pageSize = page.getSize();
    this.totalElement = (int)page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.list = page.getContent().stream().map(RecruitDto::fromEntity).collect(Collectors.toList());
  }


}
