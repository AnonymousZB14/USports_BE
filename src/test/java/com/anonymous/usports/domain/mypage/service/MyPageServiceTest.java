package com.anonymous.usports.domain.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.when;

import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.mypage.dto.MemberInfo;
import com.anonymous.usports.domain.mypage.service.impl.MyPageServiceImpl;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sportsskill.dto.SportsSkillDto;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import com.anonymous.usports.global.type.SportsGrade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

  @Mock
  private InterestedSportsRepository interestedSportsRepository;
  @Mock
  private SportsSkillRepository sportsSkillRepository;
  @Mock
  private MemberRepository memberRepository;


  @InjectMocks
  private MyPageServiceImpl myPageService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("accountName" + id)
        .name("name" + id)
        .email("test@test.com")
        .password("password" + id)
        .phoneNumber("010-1111-2222")
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();
  }

  private SportsEntity createSports(Long id) {
    return new SportsEntity(id, "football");
  }

  private RecruitEntity createRecruit(Long id, MemberEntity member, SportsEntity sports) {
    return RecruitEntity.builder()
        .recruitId(id)
        .sports(sports)
        .member(member)
        .title("title" + id)
        .content("content" + id)
        .placeName("placeName" + id)
        .lat("111")
        .lnt("100")
        .cost(10000)
        .gender(Gender.MALE)
        .currentCount(1)
        .recruitCount(10)
        .meetingDate(LocalDateTime.now())
        .recruitStatus(RecruitStatus.RECRUITING)
        .registeredAt(LocalDateTime.now())
        .gradeFrom(1)
        .gradeTo(10)
        .build();
  }

  private ParticipantEntity createParticipant(Long id, MemberEntity member, RecruitEntity recruit) {
    return ParticipantEntity.builder()
        .participantId(id)
        .member(member)
        .recruit(recruit)
        .registeredAt(LocalDateTime.now().minusDays(3L))
        .meetingDate(LocalDateTime.now().minusDays(1L))
        .status(ParticipantStatus.ACCEPTED)
        .build();
  }


  @Nested
  @DisplayName("MyPage member 데이터")
  class GetMemberInfo {

    private InterestedSportsEntity createInterestSports(Long id, MemberEntity member,
        SportsEntity sports) {
      return InterestedSportsEntity.builder()
          .interestedSportsId(id)
          .memberEntity(member)
          .sports(sports)
          .build();
    }

    @Test
    @DisplayName("성공 : 관심운동 5개")
    void getMyPageMember() {
      MemberEntity member = createMember(1L);
      List<InterestedSportsEntity> interestedSportsEntityList = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        interestedSportsEntityList.add(
            createInterestSports(10L + i, member, createSports(100L + i)));
      }
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(interestedSportsRepository.findAllByMemberEntity(member))
          .thenReturn(interestedSportsEntityList);

      //when
      MemberInfo memberInfo = myPageService.getMemberInfo(member.getMemberId());

      //then
      assertThat(memberInfo.getInterestSportsList().size()).isEqualTo(5);
    }

  }

  private SportsSkillEntity createSportsSkill(Long id, MemberEntity member, SportsEntity sports) {
    return SportsSkillEntity.builder()
        .sportsSkillId(id)
        .member(member)
        .sports(sports)
        .sportsScore(15L)
        .evaluateCount(3)
        .build();
  }

  @Test
  @DisplayName("SportsSkills")
  void getSportsSkills() {
    MemberEntity member = createMember(1L);
    List<SportsSkillEntity> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(createSportsSkill(100L + i, member, createSports(10L + i)));
    }
    //given
    when(memberRepository.findById(1L))
        .thenReturn(Optional.of(member));
    when(sportsSkillRepository.findAllByMember(member))
        .thenReturn(list);

    //when
    List<SportsSkillDto> sportsSkillDtoList = myPageService.getSportsSkills(member.getMemberId());

    //then
    for (SportsSkillDto sportsSkill : sportsSkillDtoList) {
      assertThat(sportsSkill.getSportsGrade()).isEqualTo(SportsGrade.AMATEUR_1);
    }

  }

}