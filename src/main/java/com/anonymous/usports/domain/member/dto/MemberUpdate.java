package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
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

        @NotBlank(message="이메일은 필수 입력 사항입니다")
        @Email(message="이메일 형식에 맞지 않습니다",
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$")
        private String email;

        @NotBlank(message="전화번호는 필수 입력 사항입니다")
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

        private String profileImage;

        @NotBlank(message="자주 활동하는 '시'를 꼭 입력해주세요")
        private String addrCity;

        @NotBlank(message="자주 활동하는 '동'을 꼭 입력해주세요")
        private String addrDistrict;

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
        private String profileContent;
        private String profileImage;
        private String addrCity;
        private String addrDistrict;
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
                    .profileContent(memberEntity.getProfileContent())
                    .profileImage(memberEntity.getProfileImage())
                    .addrCity(memberEntity.getAddrCity())
                    .addrDistrict(memberEntity.getAddrDistrict())
                    .build();
        }

    }
}
