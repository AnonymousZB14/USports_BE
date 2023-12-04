package com.anonymous.usports.domain.recruit.service;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitEndResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.recruit.service.impl.RecruitServiceImpl;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RecruitServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private SportsRepository sportsRepository;
  @Mock
  private RecruitRepository recruitRepository;

  @InjectMocks
  private RecruitServiceImpl recruitService;

  static final String TEST_STRING = "test";
  static MemberEntity member;
  static SportsEntity sports;
  static RecruitEntity recruit;

  @BeforeEach
  void init() {
    member = MemberEntity.builder()
        .memberId(1L)
        .accountName(TEST_STRING)
        .name(TEST_STRING)
        .email("test@test.com")
        .password(TEST_STRING)
        .phoneNumber("010-1111-2222")
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();

    sports = SportsEntity.builder()
        .sportsId(1L)
        .sportsName(TEST_STRING)
        .build();

    recruit = RecruitEntity.builder()
        .recruitId(1L)
        .sports(sports)
        .member(member)
        .title(TEST_STRING)
        .content(TEST_STRING)
        .placeName(TEST_STRING)
        .lat("111")
        .lnt("11")
        .cost(10000)
        .gender(Gender.MALE)
        .currentCount(3)
        .recruitCount(10)
        .meetingDate(LocalDateTime.now())
        .recruitStatus(RecruitStatus.RECRUITING)
        .gradeFrom(1)
        .gradeTo(10)
        .build();
  }

  @Test
  @DisplayName("Recruit 등록")
  void registerRecruit() {
    RecruitRegister.Request request =
        RecruitRegister.Request.builder()
            .sportsId(sports.getSportsId())
            .title(TEST_STRING)
            .content(TEST_STRING)
            .placeName(TEST_STRING)
            .lat("111")
            .lnt("11")
            .cost(10000)
            .recruitCount(10)
            .meetingDate(LocalDateTime.now())
            .gender(Gender.MALE)
            .gradeFrom(1)
            .gradeTo(10)
            .build();

    //given
    when(memberRepository.findById(anyLong()))
        .thenReturn(Optional.of(member));
    when(sportsRepository.findById(anyLong()))
        .thenReturn(Optional.of(sports));
    when(recruitRepository.save(any(RecruitEntity.class)))
        .thenReturn(recruit);

    //when
    RecruitDto result = recruitService.registerRecruit(request, member.getMemberId());

    //then
    assertThat(result.getSportsId()).isEqualTo(sports.getSportsId());
    assertThat(result.getRecruitStatus()).isEqualTo(RecruitStatus.RECRUITING);
  }

  @Test
  @DisplayName("Recruit 한 건 조회")
  void getRecruit() {
    Long recruitId = recruit.getRecruitId();
    //given
    when(recruitRepository.findById(anyLong()))
        .thenReturn(Optional.of(recruit));

    //when
    RecruitDto result = recruitService.getRecruit(recruitId);

    //then
    verify(recruitRepository, times(1)).findById(recruitId);

    assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());

  }

  @Test
  @DisplayName("Recruit 수정")
  void updateRecruit() {
    String UPDATE_STRING = "updated";
    RecruitUpdate.Request request =
        RecruitUpdate.Request.builder()
            .sportsId(sports.getSportsId())
            .title(TEST_STRING + UPDATE_STRING)
            .content(TEST_STRING + UPDATE_STRING)
            .placeName(TEST_STRING + UPDATE_STRING)
            .lat("111")
            .lnt("111")
            .cost(1000)
            .recruitCount(5)
            .meetingDate(LocalDateTime.now())
            .gender(Gender.FEMALE)
            .gradeFrom(1)
            .gradeTo(10)
            .build();

    RecruitEntity updatedRecruit = RecruitEntity.builder()
        .recruitId(1L)
        .sports(sports)
        .member(member)
        .title(TEST_STRING + UPDATE_STRING)
        .content(TEST_STRING + UPDATE_STRING)
        .placeName(TEST_STRING + UPDATE_STRING)
        .lat("111")
        .lnt("111")
        .cost(1000)
        .gender(Gender.FEMALE)
        .recruitCount(5)
        .meetingDate(LocalDateTime.now())
        .recruitStatus(RecruitStatus.RECRUITING)
        .gradeFrom(1)
        .gradeTo(10)
        .build();

    //given
    when(recruitRepository.findById(anyLong()))
        .thenReturn(Optional.of(recruit));
    when(sportsRepository.findById(anyLong()))
        .thenReturn(Optional.of(sports));
    when(recruitRepository.save(any(RecruitEntity.class)))
        .thenReturn(updatedRecruit);

    //when
    RecruitDto result =
        recruitService.updateRecruit(request, recruit.getRecruitId(), member.getMemberId());

    //then
    assertThat(result.getTitle()).isEqualTo(request.getTitle());
    assertThat(result.getContent()).isEqualTo(request.getContent());
    assertThat(result.getCost()).isEqualTo(request.getCost());
  }

  @Test
  @DisplayName("Recruit 삭제")
  void deleteRecruit() {
    //given
    Long recruitId = recruit.getRecruitId();
    when(recruitRepository.findById(anyLong()))
        .thenReturn(Optional.of(recruit));

    //when
    RecruitDto recruitDto = recruitService.deleteRecruit(recruitId, member.getMemberId());

    //then
    verify(recruitRepository, times(1)).delete(recruit);

    assertThat(recruitDto.getRecruitId()).isEqualTo(recruitId);
  }

  @Nested
  @DisplayName("모집 글 마감 / 마감 취소")
  class EndRecruit{
    @Test
    @DisplayName("RECRUITING -> 마감")
    void endRecruit_RECRUITING_TO_END(){
      //given
      recruit.setRecruitStatus(RecruitStatus.RECRUITING);
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_COMPLETE);
    }

    @Test
    @DisplayName("ALMOST_FINISHED -> 마감")
    void endRecruit(){
      //given
      recruit.setRecruitStatus(RecruitStatus.ALMOST_FINISHED);
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_COMPLETE);
    }

    @Test
    @DisplayName("END -> RECRUITING")
    void endRecruit_END_TO_RECRUITING(){
      //given
      recruit.setRecruitStatus(RecruitStatus.END);
      recruit.setCurrentCount(3);
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_CANCELED);
      assertThat(recruit.getRecruitStatus()).isEqualTo(RecruitStatus.RECRUITING);
    }

    @Test
    @DisplayName("END -> ALMOST_FINISHED")
    void endRecruit_END_TO_ALMOST_FINISHED(){
      //given
      recruit.setRecruitStatus(RecruitStatus.END);
      recruit.setCurrentCount(8);
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_CANCELED);
      assertThat(recruit.getRecruitStatus()).isEqualTo(RecruitStatus.ALMOST_FINISHED);
    }

  }
}