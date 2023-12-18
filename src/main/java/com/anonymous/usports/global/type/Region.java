package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Region {

  ALL("모든 지역"),
  SEOUL("서울"),
  GYEONGGI("경기"),
  INCHEON("인천"),
  GANGWON("강원"),
  DAEJEON("대전"),
  CHUNGNAM_SEJONG("충남/세종"),
  CHUNG_BOOK("충북"),
  DAEGU("대구"),
  GYEONG_BOOK("경북"),
  BUSAN("부산"),
  ULSAN("울산"),
  GYEONG_NAM("경남"),
  GWANGJU("광주"),
  JEON_NAM("전남"),
  JEON_BOOK("전북"),
  JEJU("제주");

  private final String description;
}
