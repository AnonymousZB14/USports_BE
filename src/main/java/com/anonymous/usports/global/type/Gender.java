package com.anonymous.usports.global.type;

import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.TypeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

  MALE("남성"),
  FEMALE("여성"),
  BOTH("성별 무관");

  private final String description;

  @JsonCreator
  public static Gender parsing(String inputValue) {
    return Stream.of(Gender.values())
        .filter(gender -> gender.toString().equals(inputValue.toUpperCase()))
        .findFirst()
        .orElseThrow(() -> new TypeException(ErrorCode.TYPE_INVALID_ERROR, inputValue+" : 잘못된 값 입니다."));
  }

  public static Gender of(String genderString){
    return Stream.of(Gender.values())
        .filter(g -> g.getDescription().equals(genderString))
        .findFirst()
        .orElseThrow(() -> new TypeException(ErrorCode.TYPE_INVALID_ERROR, genderString + " : 성별 텍스트가 잘못된 값입니다."));
  }
}
