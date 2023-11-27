package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.MemberStatus;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "account_name", nullable = false, unique = true)
    private String accountName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "profile_content")
    private String profileContent;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "email_auth_at", nullable = false)
    private LocalDateTime emailAuthAt;

    @Column(name = "address_city")
    private String addrCity;

    @Column(name = "address_district")
    private String addrDistrict;

    @Column(name = "profile_open", nullable = false)
    private boolean profileOpen;

    @Column(name = "manner_score")
    private Long mannerScore;

    @Column(name = "kindness_score")
    private Long kindnessScore;

    @Column(name = "passion_score")
    private Long passionScore;

    @Column(name = "teamwork_score")
    private Long teamworkScore;

    @Column(name = "evaluation_count")
    private Long evaulationCount;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
