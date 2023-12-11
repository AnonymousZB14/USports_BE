package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class MemberRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message="계정 닉네임은 필수 입력 사항입니다")
        private String accountName;

        @NotBlank(message="이름은 필수 입력 사항입니다")
        private String name;

        @NotBlank(message="이메일은 필수 입력 사항입니다")
        @Pattern(message="이메일 형식에 맞지 않습니다",
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
        private String email;

        @NotBlank(message="비밀번호는 필수 입력 사항입니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^*+=-]).{8,100}$",
                message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String password;

        @NotNull(message="성별을 입력해주세요")
        private Gender gender;

        @NotBlank(message="공개 비공개 여부를 입력해주세요, open 또는 close을 입력해주세요")
        private String profileOpen;

        public static MemberEntity toEntity(MemberRegister.Request request, LoginBy loginBy){

            String profileOpen = request.getProfileOpen().toLowerCase();
            boolean po = false;

            if (profileOpen.equals("open")) {
                po = true;
            } else if (!profileOpen.equals("close")) {
                throw new RuntimeException("Open 또는 Close를 입력해주세요");
            }

            return MemberEntity.builder()
                    .accountName(request.getAccountName())
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .gender(request.getGender())
                    .role(Role.UNAUTH)
                    .loginBy(loginBy)
                    .profileOpen(po)
                    .mannerScore(0.0)
                    .kindnessScore(0L)
                    .passionScore(0L)
                    .teamworkScore(0L)
                    .evaluationCount(0L)
                    .build();
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    private String accountName;
    private String email;
    private boolean profileOpen;
    private String message;

    public static Response fromEntity(MemberEntity memberEntity, String message) {
      return Response.builder()
          .accountName(memberEntity.getAccountName())
          .email(memberEntity.getEmail())
          .profileOpen(memberEntity.isProfileOpen())
          .message(message)
          .build();
    }

  }

}
