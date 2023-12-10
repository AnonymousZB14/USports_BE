package com.anonymous.usports.domain.sports.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "sports")
public class SportsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sports_id", nullable = false)
  private Long sportsId;

  @Column(name = "sports_name", nullable = false, unique = true)
  private String sportsName;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SportsEntity that = (SportsEntity) o;
    return Objects.equals(sportsId, that.sportsId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sportsId);
  }
}
