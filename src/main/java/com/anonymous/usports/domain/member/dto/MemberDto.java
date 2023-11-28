package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.MemberStatus;
import com.anonymous.usports.global.type.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long memberId;

    private String accountName;

    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private LocalDate birthDate;

    private Gender gender;

    private String profileContent;

    private String profileImage;

    private MemberStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime updatedAt;

    private LocalDateTime emailAuthAt;

    private String addrCity;

    private String addrDistrict;

    private boolean profileOpen;

    private Long mannerScore;

    private Long kindnessScore;

    private Long passionScore;

    private Long teamworkScore;

    private Long evaulationCount;

    private Role role;

    public static MemberDto fromEntity(MemberEntity memberEntity){
        return MemberDto.builder()
                .memberId(memberEntity.getMemberId())
                .accountName(memberEntity.getAccountName())
                .name(memberEntity.getName())
                .email(memberEntity.getEmail())
                .password(memberEntity.getPassword())
                .phoneNumber(memberEntity.getPhoneNumber())
                .birthDate(memberEntity.getBirthDate())
                .gender(memberEntity.getGender())
                .profileContent(memberEntity.getProfileContent())
                .profileImage(memberEntity.getProfileImage())
                .status(memberEntity.getStatus())
                .registeredAt(memberEntity.getRegisteredAt())
                .updatedAt(memberEntity.getUpdatedAt())
                .emailAuthAt(memberEntity.getEmailAuthAt())
                .addrCity(memberEntity.getAddrCity())
                .addrDistrict(memberEntity.getAddrDistrict())
                .profileOpen(memberEntity.isProfileOpen())
                .mannerScore(memberEntity.getMannerScore())
                .kindnessScore(memberEntity.getKindnessScore())
                .passionScore(memberEntity.getPassionScore())
                .teamworkScore(memberEntity.getTeamworkScore())
                .evaulationCount(memberEntity.getEvaulationCount())
                .role(memberEntity.getRole())
                .build();
    }
}
