package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "registered_at", nullable = false)
    @CreatedDate
    private LocalDateTime registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "email_auth_at")
    private LocalDateTime emailAuthAt;

    @Column(name = "active_region", length = 100)
    private String activeRegion;

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

    @Column(name="login_by", nullable=false)
    @Enumerated(EnumType.STRING)
    private LoginBy loginBy;


    public void updateMember(MemberUpdate.Request request) {

        String profileOpen = request.getProfileOpen().toLowerCase();

        if (profileOpen.equals("open")) {
            this.profileOpen = true;
        } else {
            this.profileOpen = false;
        }

        this.accountName = request.getAccountName();
        this.name = request.getName();
        this.phoneNumber = request.getPhoneNumber();
        this.birthDate = request.getBirthDate();
        this.gender = request.getGender();
        this.profileImage = request.getProfileImage();
        this.activeRegion = request.getActiveRegion();
        this.role = Role.USER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberEntity that = (MemberEntity) o;
        return Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}