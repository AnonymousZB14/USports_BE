package com.anonymous.usports.global.typeprovider.dto;

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

  private List<String> sportsList; //운동 종목
  private List<String> genderList; //성별
  private List<String> regionList; //지역
  private List<String> sportsLevelList; //운동 실력 지표

}
