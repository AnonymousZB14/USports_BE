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
public class RoadAddress {
  private String address_name; //전체 지번 주소
  private String region_1depth_name;//지역명1
  private String region_2depth_name;//지역명2
  private String region_3depth_name;//지역명3
  private String road_name; //도로명
  private String underground_yn; //지하 여부, Y 또는 N
  private String main_building_no; //건물 본번
  private String sub_building_no; //건물 부번, 없을 경우 빈 문자열("") 반환
  private String building_name; //건물 이름
  private String zone_no; //우편번호(5자리)
  private String x; //lnt - X 좌표값, 경위도인 경우 경도(longitude)
  private String y; //lat - Y 좌표값, 경위도인 경우 위도(latitude)
}
