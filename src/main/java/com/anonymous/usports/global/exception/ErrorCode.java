package com.anonymous.usports.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
  MEMBER_ID_UNMATCH(HttpStatus.BAD_REQUEST, "유저ID가 일치하지 않습니다"),
  ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "닉네임이 이미 존재합니다"),
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이메일이 이미 존재합니다"),
  PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "핸드폰 번호가 이미 존재합니다"),
  PASSWORD_UNMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
  NEED_AT_LEAST_ONE_SPORTS(HttpStatus.BAD_REQUEST, "최소 하나의 관심 운동이 필요합니다"),
  EMAIL_AUTH_NUMBER_EXPIRED(HttpStatus.FORBIDDEN, "회원 이메일 인증번호가 만료가 되었습니다. 재발급해주세요."),
  EMAIL_AUTH_NUMBER_UNMATCH(HttpStatus.BAD_REQUEST, "이메일 인증 번호가 다릅니다. 다시 입력해주세요"),
  NEW_PASSWORD_UNMATCH(HttpStatus.BAD_REQUEST, "새로 입력한 비밀번호가 일치하지 않습니다"),
  PHONE_NUMBER_UNMATCH(HttpStatus.BAD_REQUEST, "입력한 핸드폰 번호가 일치하지 않습니다"),
  NAME_UNMATCH(HttpStatus.BAD_REQUEST, "입력한 이름이 일치하지 않습니다"),

  // jwt 관련
  JWT_TOKEN_ERROR(HttpStatus.UNAUTHORIZED,"JWT 토큰 에러입니다"),
  JWT_EXPIRED(HttpStatus.FORBIDDEN, "JWT가 만료되었습니다"),
  JWT_TOKEN_WRONG_TYPE(HttpStatus.FORBIDDEN,"JWT 토큰 형식에 문제가 생겼습니다"),
  JWT_TOKEN_MALFORMED(HttpStatus.FORBIDDEN, "토큰이 변조가 되었습니다"),
  JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.FORBIDDEN, "Refresh 토큰을 찾을 수 없습니다"),

  //Sports 관련
  SPORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "운동 종목을 찾을 수 없습니다."),
  SPORTS_GRADE_INVALID(HttpStatus.NOT_FOUND, "Sports Grade 값이 잘못되었습니다."),

  //Recruit 관련
  RECRUIT_NOT_FOUND(HttpStatus.NOT_FOUND, "운동 모집 게시글을 찾을 수 없습니다."),
  APPLICANT_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "지원자 회원을 찾을 수 없습니다."),
  PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "모집 글 참여 신청 건을 찾을 수 없습니다."),
  RECRUIT_ALREADY_END(HttpStatus.NOT_FOUND, "이미 마감 된 모집 글 입니다."),
  RECRUIT_NOT_FINISHED(HttpStatus.NOT_FOUND, "아직 모임이 종료되지 않았습니다. 종료 후에 평가할 수 있습니다."),
  CHAT_INVITE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "모임 채팅방에 초대가 될 수 없습니다. 해당 모임에 등록하고, 승인이 되어 있는 상태여야 합니다."),

  //Evaluation 관련
  EVALUATION_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 평가가 완료된 건 입니다."),

  //Comment 관련
  COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST,"댓글을 찾을 수 없습니다."),
  CANNOT_EDIT_PARENT_COMMENT(HttpStatus.BAD_REQUEST,"원댓글은 수정할 수 없습니다."),
  CANNOT_DELETE_PARENT_COMMENT(HttpStatus.BAD_REQUEST,"원댓글은 삭제할 수 없습니다."),

  //Record 관련
  IMAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"이미지 저장 오류가 발생했습니다."),
  TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "이미지 저장 갯수를 초과했습니다."),
  IMAGE_DELETE_ERROR(HttpStatus.BAD_REQUEST,"이미지 삭제 오류가 발생했습니다."),
  RECORD_NOT_FOUND(HttpStatus.NOT_FOUND,"기록을 찾을 수 없습니다."),
  INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST,"이용할 수 없는 이미지 URL입니다."),
  UNABLE_TO_CONVERT_LIST_TO_STRING(HttpStatus.BAD_REQUEST,"이미지 리스트를 저장하는 과정에서 오류가 발생했습니다."),
  UNABLE_TO_CONVERT_STRING_TO_LIST(HttpStatus.BAD_REQUEST, "이미지 리스트를 불러오는 과정에서 오류가 발생했습니다."),
  MINIMUM_IMAGE_RESTRICT(HttpStatus.BAD_REQUEST,"적어도 한 개 이상의 이미지가 게시글에 있어야 합니다."),
  RECORD_UPDATE_ERROR(HttpStatus.BAD_REQUEST,"기록 수정 중 에러가 발생했습니다."),
  RECORD_DELETE_ERROR(HttpStatus.BAD_REQUEST,"기록 삭제 중 에러가 발생했습니다."),
  INVALID_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST,"업로드 할 수 없는 확장자입니다."),
  CANNOT_DELETE_RECORD_FOR_COMMENT(HttpStatus.BAD_REQUEST,"댓글이 있는 게시글은 삭제할 수 없습니다."),
  SELF_LIKE_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"자신의 기록을 좋아요 할 수 없습니다."),

  //Follow 관련
  FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND,"팔로우를 찾을 수 없습니다."),
  UNABLE_MANAGE_FOLLOW(HttpStatus.BAD_REQUEST, "이미 팔로우 상태입니다."),
  SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우 할 수 없습니다."),

  // Chat 관련
  CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "찾는 채팅창이 없습니다"),
  CHAT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "채팅창이 이미 존재합니다"),
  NOT_RECRUIT_HOST(HttpStatus.BAD_REQUEST, "운동 모임 작성자만 채팅방을 만들 수 있습니다"),
  MEMBER_ALREADY_IN_CHAT_ROOM(HttpStatus.BAD_REQUEST, "이미 유저가 채팅방에 있습니다"),
  USER_NOT_IN_THE_CHAT(HttpStatus.NOT_FOUND, "해당 유저는 채팅에 없습니다"),
  CANNOT_CREATE_CHAT_WITH_SAME_USER(HttpStatus.BAD_REQUEST, "내 자신과는 chat을 만들 수 없습니다"),
  NO_AVAILABLE_LIST(HttpStatus.BAD_REQUEST,"DM 에서는 불러올 수 없는 list 입니다."),

  //Type
  TYPE_INVALID_ERROR(HttpStatus.BAD_REQUEST, "Type(enum)이 잘못되었습니다."),

  // cs 관련
  NO_CS_FOUND(HttpStatus.BAD_REQUEST, "신고글이 존재하지 않습니다"),
  CS_NOT_WRITTEN_BY_CURRENT_USER(HttpStatus.UNAUTHORIZED, "해당 신고글은, 현재 로그인한 유저가 작성한 글이 아닙니다"),
  INPUT_VALUE_NOT_EXIST_FOR_CS_STATUS(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다. 1~3을 입력해주세요"),
  PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "페이지를 찾을 수 없습니다"),

  //기타
  ADDRESS_API_ERROR(HttpStatus.BAD_REQUEST, "도로명 주소 입력값이 잘못되었습니다."),

  //BASIC
  NO_AUTHORITY_ERROR(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "404 NOT FOUND"),
  BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생 했습니다.");

  private final HttpStatus statusCode;
  private final String description;

}
