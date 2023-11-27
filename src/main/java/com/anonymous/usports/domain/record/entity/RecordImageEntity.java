package com.anonymous.usports.domain.record.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity(name = "record_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordImageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id", nullable = false)
  private Long imageId;

  @ManyToOne
  @JoinColumn(name = "record_id", nullable = false)
  private RecordEntity record;

  @Column(name = "image_address", nullable = false)
  private String imageAddress;

}
