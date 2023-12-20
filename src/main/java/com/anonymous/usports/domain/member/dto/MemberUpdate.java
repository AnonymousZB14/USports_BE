package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public class MemberUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        private int emailAuthNumber;

        @NotBlank(message="계정 닉네임은 필수 입력 사항입니다")
        private String accountName;

        @NotBlank(message="이름은 필수 입력 사항입니다")
        private String name;

        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
                message = "010-0000-0000 형식으로 입력해주세요")
        private String phoneNumber;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        @NotNull(message="성별을 입력해주세요")
        private Gender gender;

        @NotBlank(message="공개 비공개 여부를 입력해주세요, open 또는 close을 입력해주세요")
        private String profileOpen;

        private String profileContent;

        @NotBlank(message="자주 활동하는 '시'를 꼭 입력해주세요")
        private String activeRegion;

        private List<Long> interestedSports;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String accountName;
        private String name;
        private String email;
        private String phoneNumber;
        private LocalDate birthDate;
        private Gender gender;
        private boolean profileOpen;
        private String profileImage;
        private String activeRegion;
        private Role role;
        private List<String> interestedSports;

        public static MemberUpdate.Response fromEntity(MemberEntity memberEntity) {
            return MemberUpdate.Response.builder()
                    .accountName(memberEntity.getAccountName())
                    .email(memberEntity.getEmail())
                    .profileOpen(memberEntity.isProfileOpen())
                    .name(memberEntity.getName())
                    .phoneNumber(memberEntity.getPhoneNumber())
                    .birthDate(memberEntity.getBirthDate())
                    .gender(memberEntity.getGender())
                    .profileImage(memberEntity.getProfileImage())
                    .activeRegion(memberEntity.getActiveRegion())
                    .role(memberEntity.getRole())
                    .build();
        }

    }
}
