package com.anonymous.usports.domain.recruit.api.dto;

import lombok.ToString;

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
@ToString
public class Document {

  private String address_name;
  private String address_type;
  private String x; //lnt : 경도
  private String y; //lat : 위도

  private Address address;
  private RoadAddress roadAddress;
}
