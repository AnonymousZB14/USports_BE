package com.anonymous.usports.domain.recruit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Meta {

  private Integer total_count; //검색어에 검색된 문서 수
  private Integer pageable_count; //total_count 중 노출 가능 문서 수(최대 : 45)
  private Boolean is_end; //현제 페이지가 마지막 페이지인지 여부

}
