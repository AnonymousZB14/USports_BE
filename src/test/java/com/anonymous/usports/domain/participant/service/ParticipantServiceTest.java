package com.anonymous.usports.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.dto.ParticipantDto;
import com.anonymous.usports.domain.participant.dto.ParticipantListDto;
import com.anonymous.usports.domain.participant.dto.ParticipantManage;
import com.anonymous.usports.domain.participant.dto.ParticipantManage.Request;
import com.anonymous.usports.domain.participant.dto.ParticipateCancel;
import com.anonymous.usports.domain.participant.dto.ParticipateResponse;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.participant.service.impl.ParticipantServiceImpl;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.ParticipantException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.ParticipantStatus;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private RecruitRepository recruitRepository;
  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private ParticipantServiceImpl participantService;

  static final String TEST_STRING = "test";
  static MemberEntity member;
  static SportsEntity sports;
  static RecruitEntity recruit;

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

  private RecruitEntity createRecruit(Long id, MemberEntity member) {
    return RecruitEntity.builder()
        .recruitId(id)
        .sports(new SportsEntity(1000L, "sportsName"))
        .member(member)
        .title("title" + id)
        .content("content" + id)
        .placeName("placeName" + id)
        .lat("111")
        .lnt("100")
        .cost(10000)
        .gender(Gender.MALE)
        .recruitCount(10)
        .meetingDate(LocalDateTime.now())
        .recruitStatus(RecruitStatus.RECRUITING)
        .gradeFrom(1)
        .gradeTo(10)
        .build();
  }

  private ParticipantEntity createParticipant(Long id, MemberEntity member, RecruitEntity recruit) {
    return ParticipantEntity.builder()
        .participantId(id)
        .member(member)
        .recruit(recruit)
        .registeredAt(LocalDateTime.now())
        .build();
  }

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
        .recruitCount(10)
        .meetingDate(LocalDateTime.now())
        .recruitStatus(RecruitStatus.RECRUITING)
        .gradeFrom(1)
        .gradeTo(10)
        .build();
  }


  @Test
  @DisplayName("Participant 리스트 조회 (페이징)")
  void getParticipants() {
    //given
    Long recruitId = recruit.getRecruitId();
    int page = 1;
    Long loginMemberId = member.getMemberId();

    List<ParticipantEntity> entityList = new ArrayList<>();
    for (long i = 1; i <= 10; i++) {
      entityList.add(ParticipantEntity.builder()
          .participantId(i)
          .member(MemberEntity.builder().memberId(i).build())
          .recruit(recruit)
          .registeredAt(LocalDateTime.now())
          .build());
    }
    when(recruitRepository.findById(anyLong()))
        .thenReturn(Optional.of(recruit));
    when(participantRepository
        .findAllByRecruitAndStatusOrderByParticipantId(any(RecruitEntity.class),
            any(ParticipantStatus.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(entityList));

    //when
    ParticipantListDto participants =
        participantService.getParticipants(recruitId, page, loginMemberId);

    //then
    assertThat(participants.getCurrentPage()).isEqualTo(1);
    assertThat(participants.getPageSize()).isEqualTo(NumberConstant.PAGE_SIZE_DEFAULT);
    assertThat(participants.getTotalPages()).isEqualTo(1);
    assertThat(participants.getTotalElements()).isEqualTo(10);
    List<ParticipantDto> list = participants.getList();
    for (int i = 0; i < list.size(); i++) {
      assertThat(list.get(i).getRecruitId()).isEqualTo(recruitId);
    }

  }

  @Nested
  @DisplayName("모집 글에 참여 신청 - joinRecruit")
  class JoinRecruit {

    @Test
    @DisplayName("신청 성공")
    void joinRecruit_JOIN_RECRUIT_COMPLETED() {
      //given
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class)))
          .thenReturn(Optional.empty());

      //when
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, times(1)).save(any(ParticipantEntity.class));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_COMPLETE);

    }

    @Test
    @DisplayName("신청 진행중인 상태")
    void joinRecruit_JOIN_RECRUIT_ING() {
      ParticipantEntity participant = ParticipantEntity.builder()
          .participantId(1L)
          .member(member)
          .recruit(recruit)
          .status(ParticipantStatus.ING)
          .registeredAt(LocalDateTime.now())
          .build();

      //given
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class)))
          .thenReturn(Optional.of(participant));

      //when
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, never()).save(any(ParticipantEntity.class));
      verify(participantRepository, times(1))
          .findByMemberAndRecruitAndStatus(
              any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ING);
    }

    @Test
    @DisplayName("이미 신청이 수락 된 상태")
    void joinRecruit_JOIN_RECRUIT_ALREADY_CONFIRMED() {
      ParticipantEntity participant = ParticipantEntity.builder()
          .participantId(1L)
          .member(member)
          .recruit(recruit)
          .status(ParticipantStatus.ACCEPTED)
          .registeredAt(LocalDateTime.now().minusHours(1L))
          .confirmedAt(LocalDateTime.now())
          .build();

      //given
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class)))
          .thenReturn(Optional.empty())
          .thenReturn(Optional.of(participant));
      //when
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, never()).save(any(ParticipantEntity.class));
      verify(participantRepository, times(2))
          .findByMemberAndRecruitAndStatus(
              any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ALREADY_ACCEPTED);
    }
  }

  @Nested
  @DisplayName("참여 신청 수락/거절")
  class ManageJoinRecruit {

    @Test
    @DisplayName("수락")
    void manageJoinRecruit_JOIN_RECRUIT_ACCEPTED() {
      ParticipantManage.Request request =
          new Request(true, member.getMemberId());
      ParticipantEntity participant = ParticipantEntity.builder()
          .participantId(1L)
          .member(member)
          .recruit(recruit)
          .registeredAt(LocalDateTime.now().minusHours(1L))
          .confirmedAt(LocalDateTime.now())
          .build();

      MemberEntity applicant = MemberEntity.builder()
          .memberId(2L)
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

      //given
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class)))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), member.getMemberId());

      //then
      verify(participantRepository, never()).delete(any(ParticipantEntity.class));
      verify(participantRepository, times(1)).save(any(ParticipantEntity.class));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ACCEPTED);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED);
    }

    @Test
    @DisplayName("거절")
    void manageJoinRecruit_JOIN_RECRUIT_REJECTED() {
      ParticipantManage.Request request =
          new Request(false, member.getMemberId());
      ParticipantEntity participant = ParticipantEntity.builder()
          .participantId(1L)
          .member(member)
          .recruit(recruit)
          .registeredAt(LocalDateTime.now().minusHours(1L))
          .confirmedAt(LocalDateTime.now())
          .build();

      MemberEntity applicant = MemberEntity.builder()
          .memberId(2L)
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

      //given
      when(memberRepository.findById(anyLong()))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(anyLong()))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          any(MemberEntity.class), any(RecruitEntity.class), any(ParticipantStatus.class)))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), member.getMemberId());

      //then
      verify(participantRepository, times(1)).save(any(ParticipantEntity.class));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_REJECTED);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.REFUSED);
    }
  }

  @Nested
  @DisplayName("참여 신청 취소")
  class CancelJoinRecruit {

    @Test
    @DisplayName("정상 - ING 상태의 경우")
    void cancelJoinRecruit_ING() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);
      ParticipantEntity participant = createParticipant(100L, member, recruit);
      participant.setStatus(ParticipantStatus.ING);
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(member, recruit,
          ParticipantStatus.ING))
          .thenReturn(Optional.of(participant));

      //when
      ParticipateCancel result =
          participantService.cancelJoinRecruit(recruit.getRecruitId(), member.getMemberId());

      //then
      verify(participantRepository, times(1)).delete(participant);
      assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(result.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(result.getMessage()).isEqualTo(ResponseConstant.CANCEL_JOIN_RECRUIT);
    }

    @Test
    @DisplayName("정상 - ACCEPTED 상태의 경우")
    void cancelJoinRecruit_ACCEPTED() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);
      ParticipantEntity participant = createParticipant(100L, member, recruit);
      participant.setStatus(ParticipantStatus.ACCEPTED);
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(member, recruit,
          ParticipantStatus.ING))
          .thenReturn(Optional.empty());
      when(participantRepository.findByMemberAndRecruitAndStatus(member, recruit,
          ParticipantStatus.ACCEPTED))
          .thenReturn(Optional.of(participant));
      //when
      ParticipateCancel result =
          participantService.cancelJoinRecruit(recruit.getRecruitId(), member.getMemberId());

      //then
      verify(participantRepository, times(1)).delete(participant);
      assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(result.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(result.getMessage()).isEqualTo(ResponseConstant.CANCEL_JOIN_RECRUIT);
    }

    @Test
    @DisplayName("실패 - 해당 Recruit에 대한 신청 내역이 존재하지 않음")
    void cancelJoinRecruit_PARTICIPANT_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);
      ParticipantEntity participant = createParticipant(100L, member, recruit);
      participant.setStatus(ParticipantStatus.ACCEPTED);
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(member, recruit,
          ParticipantStatus.ING))
          .thenReturn(Optional.empty());
      when(participantRepository.findByMemberAndRecruitAndStatus(member, recruit,
          ParticipantStatus.ACCEPTED))
          .thenReturn(Optional.empty());

      //when
      ParticipantException exception =
          catchThrowableOfType(() ->
              participantService.cancelJoinRecruit(
                  recruit.getRecruitId(), member.getMemberId()), ParticipantException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PARTICIPANT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - APPLICANT_MEMBER_NOT_FOUND")
    void cancelJoinRecruit_APPLICANT_MEMBER_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);
      ParticipantEntity participant = createParticipant(100L, member, recruit);
      participant.setStatus(ParticipantStatus.ACCEPTED);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      MemberException exception =
          catchThrowableOfType(() ->
              participantService.cancelJoinRecruit(
                  recruit.getRecruitId(), member.getMemberId()), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.APPLICANT_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void cancelJoinRecruit_RECRUIT_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);
      ParticipantEntity participant = createParticipant(100L, member, recruit);
      participant.setStatus(ParticipantStatus.ACCEPTED);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      RecruitException exception =
          catchThrowableOfType(() ->
              participantService.cancelJoinRecruit(
                  recruit.getRecruitId(), member.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

  }

}