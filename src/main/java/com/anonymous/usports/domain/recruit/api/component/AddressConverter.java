package com.anonymous.usports.domain.recruit.api.component;

import com.anonymous.usports.domain.recruit.api.dto.AddressDto;
import com.anonymous.usports.domain.recruit.api.dto.AddressResponse;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AddressConverter {

  @Value("${kakao.api_key}")
  private String kakaoApiKey;

  public AddressDto roadNameAddressToLocationInfo(String roadNameAddress) {
    String reqUrl = new StringBuilder()
        .append("https://dapi.kakao.com/v2/local/search/address.json")
        .append("?")
        .append("query=").append(roadNameAddress).append("&")
        .append("analyze_type=exact")
        .toString();

    RestTemplate rt = new RestTemplate();
    rt.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    //HttpHeader
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "KakaoAK " + kakaoApiKey);

    HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

    ResponseEntity<String> response =
        rt.exchange(reqUrl, HttpMethod.GET, httpEntity, String.class);
    log.info(response.toString());
    Gson gson = new Gson();
    AddressResponse addressResponse = gson.fromJson(response.getBody(), AddressResponse.class);

    log.info(addressResponse.toString());

    return AddressDto.fromAddressResponse(addressResponse);
  }
}
