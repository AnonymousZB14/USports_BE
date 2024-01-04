package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.global.type.Gender;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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

        @NotBlank(message="자주 활동하는 '시'를 꼭 입력해주세요")
        private String activeRegion;

        private List<Long> interestedSportsList;
  }

}
