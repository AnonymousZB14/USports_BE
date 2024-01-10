package com.anonymous.usports.domain.record.entity;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.config.StringListConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert //TODO @ColumnDefault가 create할 때 작동하기 때문에 넣었습니다. 나중에 제거할 생각입니다.
public class RecordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "record_id", nullable = false)
  private Long recordId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sports_id", nullable = false)
  private SportsEntity sports;

  @Column(name = "content", nullable = false)
  private String recordContent;

  @Column(name = "registered_at", nullable = false)
  @CreatedDate
  private LocalDateTime registeredAt;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "count_comment", nullable = false)
  @ColumnDefault("0")
  private Long countComment;

  @Column(name = "count_recordlikes", nullable = false)
  @ColumnDefault("0")
  private Long countRecordLikes;

  @Convert(converter = StringListConverter.class)
  @Builder.Default
  @Column(name = "image_address", columnDefinition = "TEXT")
  private List<String> imageAddress = new ArrayList<>();

  @Convert(converter = StringListConverter.class)
  @Builder.Default
  @Column(name = "thumbnail_image_address", columnDefinition = "TEXT")
  private List<String> thumbnailAddress = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecordEntity record = (RecordEntity) o;
    return Objects.equals(recordId, record.recordId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recordId);
  }
}
