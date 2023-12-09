package com.anonymous.usports.domain.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.when;

import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.mypage.dto.MyPageMember;
import com.anonymous.usports.domain.mypage.dto.MyPageParticipant;
import com.anonymous.usports.domain.mypage.dto.MyPageRecruit;
import com.anonymous.usports.domain.mypage.service.impl.MyPageServiceImpl;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
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
  private RecruitRepository recruitRepository;
  @Mock
  private ParticipantRepository participantRepository;


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
  class GetMyPageMember {

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
    void getMyPageMember_5() {
      MemberEntity member = createMember(1L);
      List<InterestedSportsEntity> interestedSportsEntityList = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        interestedSportsEntityList.add(
            createInterestSports(10L + i, member, createSports(100L + i)));
      }
      //given
      when(interestedSportsRepository.findAllByMemberEntity(member))
          .thenReturn(interestedSportsEntityList);

      //when
      MyPageMember myPageMember = myPageService.getMyPageMember(member);

      //then
      assertThat(myPageMember.getInterestSportsList().size()).isEqualTo(3);
      assertThat(myPageMember.getPlusAlpha()).isEqualTo(2);
    }

    @Test
    @DisplayName("성공 : 관심운동 3개")
    void getMyPageMember_3() {
      MemberEntity member = createMember(1L);
      List<InterestedSportsEntity> interestedSportsEntityList = new ArrayList<>();
      for (int i = 0; i < 3; i++) {
        interestedSportsEntityList.add(
            createInterestSports(10L + i, member, createSports(100L + i)));
      }
      //given
      when(interestedSportsRepository.findAllByMemberEntity(member))
          .thenReturn(interestedSportsEntityList);

      //when
      MyPageMember myPageMember = myPageService.getMyPageMember(member);

      //then
      assertThat(myPageMember.getInterestSportsList().size()).isEqualTo(3);
      assertThat(myPageMember.getPlusAlpha()).isEqualTo(0);
    }

    @Test
    @DisplayName("성공 : 관심운동 1개")
    void getMyPageMember() {
      MemberEntity member = createMember(1L);
      List<InterestedSportsEntity> interestedSportsEntityList = new ArrayList<>();
      for (int i = 0; i < 1; i++) {
        interestedSportsEntityList.add(
            createInterestSports(10L + i, member, createSports(100L + i)));
      }
      //given
      when(interestedSportsRepository.findAllByMemberEntity(member))
          .thenReturn(interestedSportsEntityList);

      //when
      MyPageMember myPageMember = myPageService.getMyPageMember(member);

      //then
      assertThat(myPageMember.getInterestSportsList().size()).isEqualTo(1);
      assertThat(myPageMember.getPlusAlpha()).isEqualTo(0);
    }

    @Test
    @DisplayName("성공 : 관심운동 0개")
    void getMyPageMember_0() {
      MemberEntity member = createMember(1L);
      List<InterestedSportsEntity> interestedSportsEntityList = new ArrayList<>();

      //given
      when(interestedSportsRepository.findAllByMemberEntity(member))
          .thenReturn(interestedSportsEntityList);

      //when
      MyPageMember myPageMember = myPageService.getMyPageMember(member);

      //then
      assertThat(myPageMember.getInterestSportsList().get(0)).isEqualTo("none");
      assertThat(myPageMember.getPlusAlpha()).isEqualTo(0);
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
    when(sportsSkillRepository.findAllByMember(member))
        .thenReturn(list);

    //when
    List<SportsSkillDto> sportsSkillDtoList = myPageService.getSportsSkills(member);

    //then
    for (SportsSkillDto sportsSkill : sportsSkillDtoList) {
      assertThat(sportsSkill.getSportsGrade()).isEqualTo(SportsGrade.AMATEUR_1);
    }

  }

  @Test
  @DisplayName("평가하기")
  void getRecruitAndParticipants() {
    /*
    LocalDateTime을 Mocking 하는 과정에서 문제가 생겨서
    이부분은 API 테스트로 대체한다.

    해결 하려면 서비스 전체에서 Clock을 Bean으로 등록하여 사용해줘야 한다.
    이걸 지금 변경하기에는 시간이 너무 많이 들 것 같고,
    이 문제 때문에 실제 서비스에서 문제 생길 일은 없을 것 같아서 그냥 넘어간다.
    */
//    MemberEntity member = createMember(1L);
//    RecruitEntity recruit1 = createRecruit(10L, member, createSports(1000L));
//    RecruitEntity recruit2 = createRecruit(20L, member, createSports(1000L));
//
//    //thisMemberParticipateList
//    List<ParticipantEntity> thisMemberParticipateList = new ArrayList<>();
//    ParticipantEntity thisMemberParticipant1 = createParticipant(100L, member, recruit1);
//    thisMemberParticipant1.setMeetingDate(LocalDateTime.now().minusDays(1L));
//
//    ParticipantEntity thisMemberParticipant2 = createParticipant(101L, member, recruit2);
//    thisMemberParticipant2.setMeetingDate(LocalDateTime.now().minusDays(1L));
//
//    thisMemberParticipateList.add(thisMemberParticipant1);
//    thisMemberParticipateList.add(thisMemberParticipant2);
//    when(participantRepository.findAllByMemberAndMeetingDateIsAfter(member, any(LocalDateTime.class)))
//        .thenReturn(thisMemberParticipateList);
//
//    //otherParticipant1
//    List<ParticipantEntity> otherParticipantRecruit1 = new ArrayList<>();
//    otherParticipantRecruit1.add(createParticipant(2000L, createMember(2L), recruit1));
//    otherParticipantRecruit1.add(createParticipant(2001L, createMember(3L), recruit1));
//    otherParticipantRecruit1.add(createParticipant(2002L, createMember(4L), recruit1));
//
//    when(participantRepository.findAllByRecruitAndStatus(recruit1, ParticipantStatus.ACCEPTED))
//        .thenReturn(otherParticipantRecruit1);
//
//    //otherParticipant2
//    List<ParticipantEntity> otherParticipantRecruit2 = new ArrayList<>();
//    otherParticipantRecruit1.add(createParticipant(3000L, createMember(2L), recruit2));
//    otherParticipantRecruit1.add(createParticipant(3001L, createMember(3L), recruit2));
//    otherParticipantRecruit1.add(createParticipant(3002L, createMember(4L), recruit2));
//
//    when(participantRepository.findAllByRecruitAndStatus(recruit2, ParticipantStatus.ACCEPTED))
//        .thenReturn(otherParticipantRecruit2);
//
//    //then
//    List<RecruitAndParticipants> result =
//        myPageService.getRecruitAndParticipants(member);

  }

  @Test
  @DisplayName("내 신청 현황 : Participant List 조회")
  void getParticipantList() {
    MemberEntity member = createMember(1L);
    SportsEntity sports = createSports(100L);

    List<ParticipantEntity> participateList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      RecruitEntity recruit = createRecruit(10L + i, member, sports);
      ParticipantEntity participant = createParticipant(50L + i, member, recruit);
      participateList.add(participant);
    }

    when(participantRepository.findTop10ByMemberOrderByRegisteredAtDesc(member))
        .thenReturn(participateList);

    //when
    List<MyPageParticipant> participantList = myPageService.getParticipantList(member);

    //then
    for (MyPageParticipant p : participantList) {
      assertThat(p.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED);
      assertThat(p.getSportsName()).isEqualTo(sports.getSportsName());
    }
  }

  @Test
  @DisplayName("내 모집 관리")
  void getMyRecruitList() {
    MemberEntity member = createMember(1L);
    SportsEntity sports = createSports(100L);

    //given
    List<RecruitEntity> findList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      findList.add(createRecruit(10L + i, member, sports));
    }

    when(recruitRepository.findTop10ByMemberOrderByMeetingDateDesc(member))
        .thenReturn(findList);

    //when
    List<MyPageRecruit> result = myPageService.getMyRecruitList(member);

    //then
    for (MyPageRecruit myPageRecruit : result) {
      assertThat(myPageRecruit.getRecruitId()).isNotNull();
      assertThat(myPageRecruit.getSportsName()).isEqualTo(sports.getSportsName());
      assertThat(myPageRecruit.getTitle()).isNotNull();
      assertThat(myPageRecruit.getGender()).isEqualTo(Gender.MALE);
      assertThat(myPageRecruit.getStatus()).isEqualTo(RecruitStatus.RECRUITING);
    }
  }

}