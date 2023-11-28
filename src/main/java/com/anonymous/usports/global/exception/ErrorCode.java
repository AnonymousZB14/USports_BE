package com.anonymous.usports.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "회원을 찾을 수 없습니다."),
  MEMBER_ID_UNMATCH(HttpStatus.BAD_REQUEST.value(), "유저ID가 일치하지 않습니다"),
  ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "닉네임이 이미 존재합니다"),
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이메일이 이미 존재합니다"),
  PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "핸드폰 번호가 이미 존재합니다"),
  PASSWORD_UNMATCH(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다"),
  NEED_AT_LEAST_ONE_SPORTS(HttpStatus.BAD_REQUEST.value(), "최소 하나의 관심 운동이 필요합니다"),

  // jwt 관련
  JWT_EXPIRED(HttpStatus.FORBIDDEN.value(), "JWT가 만료되었습니다"),
  JWT_TOKEN_WRONG_TYPE(HttpStatus.FORBIDDEN.value(),"JWT 토큰 형식에 문제가 생겼습니다"),
  JWT_TOKEN_MALFORMED(HttpStatus.FORBIDDEN.value(), "토큰이 변조가 되었습니다"),

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
