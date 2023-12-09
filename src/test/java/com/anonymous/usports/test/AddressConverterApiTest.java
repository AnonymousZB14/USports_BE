package com.anonymous.usports.test;


import static org.assertj.core.api.Assertions.assertThat;

import com.anonymous.usports.domain.recruit.api.component.AddressConverter;
import com.anonymous.usports.domain.recruit.api.dto.AddressDto;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AddressConverterApiTest {

  @Autowired
  private AddressConverter addressConverter;

  @Test
  @DisplayName("성공")
  void addressConverter() {
    String name = "전북 삼성동 100";
    AddressDto addressDto = addressConverter.roadNameAddressToBuildingInfo(name);
    log.info(addressDto.toString());
  }

  @Test
  @DisplayName("잘못된 주소 입력")
  void addressConverter_ADDRESS_API_ERROR() {
    String addr = "전남 삼!성!동! 1!0";
    try {
      addressConverter.roadNameAddressToBuildingInfo(addr);
    } catch (MyException e) {
      assertThat(e.getErrorCode()).isEqualTo(ErrorCode.ADDRESS_API_ERROR);
    }

  }
}
