package com.anonymous.usports.domain.member.dto.frontResponse;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.sports.dto.SportsDto;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {

  private Long memberId;

  private String accountName;

  private String name;

  private String email;

  private String phoneNumber;

  private LocalDate birthDate;

  private Gender gender;

  private String profileImage;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  private LocalDateTime emailAuthAt;

  private String activeRegion;

  private boolean profileOpen;

  private Double mannerScore;

  private Long kindnessScore;

  private Long passionScore;

  private Long teamworkScore;

  private Long evaluationCount;

  private Long penaltyCount;

  private Role role;

  private LoginBy loginBy;

  private List<SportsDto> interestedSportsList;

  public static MemberResponse fromDto(MemberDto member, List<SportsDto> interestedSportsList){
    return MemberResponse.builder()
        .memberId(member.getMemberId())
        .accountName(member.getAccountName())
        .name(member.getName())
        .email(member.getEmail())
        .phoneNumber(member.getPhoneNumber())
        .birthDate(member.getBirthDate())
        .gender(member.getGender())
        .profileImage(member.getProfileImage())
        .registeredAt(member.getRegisteredAt())
        .updatedAt(member.getUpdatedAt())
        .emailAuthAt(member.getEmailAuthAt())
        .activeRegion(member.getActiveRegion())
        .profileOpen(member.isProfileOpen())
        .mannerScore(member.getMannerScore())
        .kindnessScore(member.getKindnessScore())
        .passionScore(member.getPassionScore())
        .teamworkScore(member.getTeamworkScore())
        .evaluationCount(member.getEvaluationCount())
        .penaltyCount(member.getPenaltyCount())
        .role(member.getRole())
        .loginBy(member.getLoginBy())
        .interestedSportsList(interestedSportsList)
        .build();
  }

}
