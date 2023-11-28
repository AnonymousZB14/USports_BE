package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.MemberStatus;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

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
        @Email(message="이메일 형식에 맞지 않습니다",
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
        private String email;

        @NotBlank(message="비밀번호는 필수 입력 사항입니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^*+=-]).{8,100}$",
                message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String password;

        @NotBlank(message="전화번호는 필수 입력 사항입니다")
        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
                message = "010-0000-0000 형식으로 입력해주세요")
        private String phoneNumber;

        @NotBlank(message="생년월일을 입력해주세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        @NotBlank(message="성별을 입력해주세요")
        @Pattern(regexp = "male|female", message = "male 또는 female을 입력해주세요")
        private Gender gender;

        @NotBlank(message="공개 비공개 여부를 입력해주세요, open 또는 close을 입력해주세요")
        private String profileOpen;

        public static MemberEntity toEntity(MemberRegister.Request request){

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
                    .phoneNumber(request.getPhoneNumber())
                    .birthDate(request.getBirthDate())
                    .gender(request.getGender())
                    .status(MemberStatus.NEED_UPDATE)
                    .role(Role.USER)
                    .profileOpen(po)
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

        public static Response fromEntity(MemberEntity memberEntity) {
            return Response.builder()
                    .accountName(memberEntity.getAccountName())
                    .email(memberEntity.getEmail())
                    .profileOpen(memberEntity.isProfileOpen())
                    .build();
        }

    }

}