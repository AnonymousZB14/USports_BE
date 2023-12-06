package com.anonymous.usports.global.exception;

import com.anonymous.usports.global.EvaluationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

  @ExceptionHandler(MyException.class)
  protected ResponseEntity<ErrorResponse> myException(MyException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(RecordException.class)
  public ResponseEntity<ErrorResponse> handleRecordException(RecordException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(FollowException.class)
  public ResponseEntity<ErrorResponse> handleFollowException(FollowException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(RecruitException.class)
  public ResponseEntity<ErrorResponse> handleRecordException(RecruitException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(ParticipantException.class)
  public ResponseEntity<ErrorResponse> handleRecordException(ParticipantException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(EvaluationException.class)
  public ResponseEntity<ErrorResponse> handleEvaluationException(EvaluationException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }



}
