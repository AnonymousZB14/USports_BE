package com.anonymous.usports.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "회원을 찾을 수 없습니다."),
  SPORTS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "운동 종목을 찾을 수 없습니다."),

  //BASIC
  NO_AUTHORITY_ERROR(HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),
  NOT_FOUND_ERROR(HttpStatus.NOT_FOUND.value(), "404 NOT FOUND"),
  BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 서버 오류가 발생 했습니다.");

  private final int statusCode;
  private final String description;

}
