package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.evaluation.dto.MannerDto;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.LoginBy;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
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

  @Column(name = "gender")
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
    this.activeRegion = request.getActiveRegion();
    this.role = Role.USER;
  }
  public void updateMemberProfileImage(String profileImageAddress) {
    this.profileImage = profileImageAddress;
  }

  /**
   * manner 점수 관련 update - kindnessScore, passionScore, teamworkScore은 모두 쌓아나감 - mannserScore는 실제 매너
   * 점수 값을 1~10 사이의 double 값으로 나타냄
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

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    MemberEntity that = (MemberEntity) object;
    return Objects.equals(memberId, that.memberId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(memberId);
  }
}