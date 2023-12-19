package com.anonymous.usports.domain.customerservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CsListDto {

  private int pageNum;
  private int totalPageNum;
  private int elementsInPage;
  private int allElementsCount;
  private List<CsDto> csList;
}
