package com.anonymous.usports.global.config;

import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.RecordException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper mapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
      //ObjectMapper가 JSON 데이터를 Java 객체로 역직렬화할 때, JSON 데이터에 존재하지 않는 속성이 Java 객체에 정의된 경우 무시하고 객체 생성하는 옵션
      .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
      //ObjectMapper가 JSON 데이터를 Java 객체로 역직렬화할 때, JSON 데이터에 null 값이 포함된 기본형(primitive type)필드에 대해서 예외를 발생시키지 않도록 하는 옵션

  // DB에 저장 될 때 사용
  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    try {
      return mapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      log.debug("StringListConverter.convertToDatabaseColumn exception occur attribute: {}", attribute.toString());
      throw new RecordException(ErrorCode.UNABLE_TO_CONVERT_LIST_TO_STRING);
    }
  }

  // DB의 데이터를 Object로 매핑할 때 사용
  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      return mapper.readValue(dbData, List.class);
    } catch (IOException e) {
      log.debug("StringListConverter.convertToEntityAttribute exception occur dbData: {}", dbData);
      throw new RecordException(ErrorCode.UNABLE_TO_CONVERT_STRING_TO_LIST);
    }
  }
}
