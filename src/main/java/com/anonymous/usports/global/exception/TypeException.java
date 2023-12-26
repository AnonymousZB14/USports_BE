package com.anonymous.usports.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class TypeException extends RuntimeException {

  private ErrorCode errorCode;
  private String errorMessage;

  public TypeException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getDescription();
  }

  public TypeException(ErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.errorMessage = message;
  }


}
