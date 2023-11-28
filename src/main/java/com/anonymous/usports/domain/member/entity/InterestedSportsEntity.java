package com.anonymous.usports.domain.member.entity;

import com.anonymous.usports.domain.sports.entity.SportsEntity;
import lombok.*;

import javax.persistence.*;

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
}
