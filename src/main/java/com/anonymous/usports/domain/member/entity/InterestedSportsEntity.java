package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "interested_sports")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class InterestedSportsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interestedSportsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sports_id", nullable = false)
    private SportsEntity sports;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestedSportsEntity that = (InterestedSportsEntity) o;
        return Objects.equals(interestedSportsId, that.interestedSportsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestedSportsId);
    }
}
