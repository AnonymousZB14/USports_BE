package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.MemberStatus;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "Member")
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

    @Column(name = "account_name", nullable = false, unique = true, length = 100)
    private String accountName;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true, length = 100)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "profile_content", length = 100)
    private String profileContent;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "registered_at", nullable = false)
    @CreatedDate
    private LocalDateTime registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "email_auth_at")
    private LocalDateTime emailAuthAt;

    @Column(name = "address_city", length = 100)
    private String addrCity;

    @Column(name = "address_district", length = 100)
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
    private Long evaluationCount;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public void updateMember(MemberUpdate.Request request) {

        String profileOpen = request.getProfileOpen().toLowerCase();

        if (profileOpen.equals("open")) {
            this.profileOpen = true;
        } else {
            this.profileOpen = false;
        }

        this.accountName = request.getAccountName();
        this.name = request.getName();
        this.email = request.getEmail();
        this.phoneNumber = request.getPhoneNumber();
        this.birthDate = request.getBirthDate();
        this.gender = request.getGender();
        this.profileContent = request.getProfileContent();
        this.profileImage = request.getProfileImage();
        this.addrCity = request.getAddrCity();
        this.addrDistrict = request.getAddrDistrict();
    }
}