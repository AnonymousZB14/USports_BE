package com.anonymous.usports.global.typeprovider.dto;

import com.anonymous.usports.domain.sports.dto.SportsDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeList {

  private List<String> genderList; //성별
  private List<String> regionList; //지역
  private List<SportsDto> sportsList; //운동 종목
  private List<SportsLevelDto> sportsLevelList; //운동 실력 지표
  private List<RecruitStatusDto> recruitStatusList;//모집 상태
}
