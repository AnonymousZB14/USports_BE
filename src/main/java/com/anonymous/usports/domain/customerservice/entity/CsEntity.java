package com.anonymous.usports.domain.customerservice.entity;

import com.anonymous.usports.domain.customerservice.dto.UpdateCS;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.global.type.CsStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "customer_service")
@EntityListeners(AuditingEntityListener.class)
public class CsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long csId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="member_id", nullable = false)
  private MemberEntity memberEntity;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "status", nullable = false)
  private CsStatus csStatus;

  @CreatedDate
  @Column(name="registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public void updateCs(UpdateCS.Request request, CsStatus csStatus) {
    this.title = request.getTitle();
    this.content = request.getContent();
    this.csStatus = csStatus;
  }

  public void updateStatus(CsStatus csStatus){
    this.csStatus = csStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CsEntity csEntity = (CsEntity) o;
    return Objects.equals(csId, csEntity.csId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(csId);
  }
}
