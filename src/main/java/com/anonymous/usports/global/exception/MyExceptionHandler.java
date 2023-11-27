package com.anonymous.usports.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

  @ExceptionHandler(MyException.class)
  protected ErrorResponse myException(MyException e) {
    return new ErrorResponse(e.getErrorCode());
  }


}
