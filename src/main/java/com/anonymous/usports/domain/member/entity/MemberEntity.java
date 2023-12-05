package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.evaluation.dto.MannerDto;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
  private Double mannerScore;

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
    this.role = Role.USER;
  }

  /**
   * manner 점수 관련 update
   * - kindnessScore, passionScore, teamworkScore은 모두 쌓아나감
   * - mannserScore는 실제 매너 점수 값을 1~10 사이의 double 값으로 나타냄
   */
  public void updateManners(MannerDto mannerDto) {
    this.kindnessScore += mannerDto.getKindness();
    this.passionScore += mannerDto.getPassion();
    this.teamworkScore += mannerDto.getTeamwork();
    Long currentCount = this.evaluationCount;

    int total = mannerDto.getKindness() + mannerDto.getPassion() + mannerDto.getTeamwork();

    this.mannerScore =
        ((this.mannerScore * currentCount) + (double) total / 3) / (currentCount + 1);

    this.evaluationCount += 1;
  }
}