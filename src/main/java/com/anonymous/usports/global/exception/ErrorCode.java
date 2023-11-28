package com.anonymous.usports.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "회원을 찾을 수 없습니다."),

  //Sports 관련
  SPORTS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "운동 종목을 찾을 수 없습니다."),
  //Recruit 관련
  RECRUIT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "운동 종목을 찾을 수 없습니다."),

  //Record 관련
  IMAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"이미지 저장 오류가 발생했습니다."),
  TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST.value(), "이미지 저장 갯수를 초과했습니다."),


  //BASIC
  NO_AUTHORITY_ERROR(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),
  NOT_FOUND_ERROR(HttpStatus.NOT_FOUND.value(), "404 NOT FOUND"),
  BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 서버 오류가 발생 했습니다.");

  private final int statusCode;
  private final String description;

}
