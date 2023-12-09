package com.anonymous.usports.test;


import com.anonymous.usports.domain.recruit.api.component.AddressConverter;
import com.anonymous.usports.domain.recruit.api.dto.AddressDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AddressConverterApiTest {

  @Autowired
  private AddressConverter addressConverter;

  @Test
  void addressConverter(){
    String name = "강동구 상암로 aa1";
    AddressDto addressDto = addressConverter.roadNameAddressToBuildingInfo(name);
  }
}
