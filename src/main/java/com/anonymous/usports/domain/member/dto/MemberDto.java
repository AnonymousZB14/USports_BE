package com.anonymous.usports.domain.member.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto implements UserDetails {

    private Long memberId;

    private String accountName;

    private String name;

    private String email;

    private String password;

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

    private Map<String, Object> attributes;

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
                .profileImage(memberEntity.getProfileImage())
                .registeredAt(memberEntity.getRegisteredAt())
                .updatedAt(memberEntity.getUpdatedAt())
                .emailAuthAt(memberEntity.getEmailAuthAt())
                .activeRegion(memberEntity.getActiveRegion())
                .profileOpen(memberEntity.isProfileOpen())
                .mannerScore(memberEntity.getMannerScore())
                .kindnessScore(memberEntity.getKindnessScore())
                .passionScore(memberEntity.getPassionScore())
                .teamworkScore(memberEntity.getTeamworkScore())
                .evaluationCount(memberEntity.getEvaluationCount())
                .penaltyCount(memberEntity.getPenaltyCount())
                .role(memberEntity.getRole())
                .loginBy(memberEntity.getLoginBy())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();

        auth.add(new SimpleGrantedAuthority("ROLE_" + this.role));

        return auth;
    }

    @Override
    public String getUsername() {
        return this.accountName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
