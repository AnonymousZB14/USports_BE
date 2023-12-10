package com.anonymous.usports.domain.recruit.api.dto;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MyException;
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
public class AddressDto {

  private String roadNameAddress;
  private String roadNumberAddress;

  private String lat;
  private String lnt;
  private String hCode;//행정 코드
  private String bCode;//법정 코드


  public static AddressDto fromAddressResponse(AddressResponse addressResponse){

    try {
      Document document = addressResponse.getDocuments().get(0);
      return AddressDto.builder()
          .roadNameAddress(document.getAddress_name())
          .roadNumberAddress(document.getAddress().getAddress_name())
          .lat(document.getY())
          .lnt(document.getX())
          .bCode(document.getAddress().getB_code())
          .hCode(document.getAddress().getH_code())
          .build();
    }catch (Exception e){
      throw new MyException(ErrorCode.ADDRESS_API_ERROR);
    }
  }

}
