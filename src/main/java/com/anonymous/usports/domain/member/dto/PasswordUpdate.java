package com.anonymous.usports.domain.member.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PasswordUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message="기본 비밀번호는 필수 입력 사항입니다")
        private String currentPassword;

        @NotBlank(message="새로운 비밀번호는 필수 입력 사항입니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^*+=-]).{8,100}$",
                message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String newPassword;

        @NotBlank(message="비밀번호 확인은 필수 입력 사항입니다")
        private String newPasswordCheck;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String message;
    }
}
