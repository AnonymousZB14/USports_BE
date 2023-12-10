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
public class Address {

  private String address_name; //전체 지번 주소
  private String region_1depth_name;//지역 1 Depth, 시도 단위
  private String region_2depth_name;//지역 2 Depth, 구 단위
  private String region_3depth_name;//지역 3 Depth, 동 단위
  private String region_3depth_h_name;//지역 3 Depth, 행정동 명칭
  private String h_code;//행정 코드
  private String b_code;//법정 코드
  private String mountain_yn;//산 여부, Y 또는 N
  private String main_address_no;//지번 주번지
  private String sub_address_no;//지번 부번지, 없을 경우 빈 문자열("") 반환
  private String x; //lnt - X 좌표값, 경위도인 경우 경도(longitude)
  private String y; //lat - Y 좌표값, 경위도인 경우 위도(latitude)
}
