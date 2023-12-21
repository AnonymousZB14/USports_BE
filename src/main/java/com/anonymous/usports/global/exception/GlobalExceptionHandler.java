package com.anonymous.usports.global.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.NO_AUTHORITY_ERROR, e.getMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getErrorCode().getStatusCode());
  }

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

  @ExceptionHandler(CsException.class)
  public ResponseEntity<ErrorResponse> handleCsException(CsException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.JWT_TOKEN_ERROR, e.getMessage());
    return new ResponseEntity<>(errorResponse, ErrorCode.JWT_TOKEN_ERROR.getStatusCode());
  }

  @ExceptionHandler(ChatException.class)
  public ResponseEntity<ErrorResponse> handleChatException(ChatException e) {
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

  @ExceptionHandler(CommentException.class)
  public ResponseEntity<ErrorResponse> handleRecordException(CommentException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

  @ExceptionHandler(TypeException.class)
  public ResponseEntity<ErrorResponse> handleTypeException(TypeException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatusCode());
  }

}
