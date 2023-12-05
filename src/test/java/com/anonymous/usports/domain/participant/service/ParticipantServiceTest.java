package com.anonymous.usports.domain.participant.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.any;
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
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


@Slf4j

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
        .currentCount(1)
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

  @Nested
  @DisplayName("신청중(ING)인 Participant 리스트 조회 (페이징)")
  class GetParticipants {

    @Test
    @DisplayName("성공")
    void getParticipants() {
      //given
      MemberEntity recruitWriter = createMember(999L);
      RecruitEntity recruit = createRecruit(10L, recruitWriter);
      List<ParticipantEntity> participantList = new ArrayList<>();

      for (long i = 1; i <= 7; i++) {
        MemberEntity memberEntity = createMember(i);
        ParticipantEntity participant = createParticipant(i + 100, memberEntity, recruit);
        participantList.add(participant);
      }

      int page = 2;

      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository
          .findAllByRecruitAndStatusOrderByParticipantId(
              recruit,
              ParticipantStatus.ING,
              PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT))
      ).thenReturn(new PageImpl<>(participantList));

      //when
      ParticipantListDto participants =
          participantService.getParticipants(recruit.getRecruitId(), page,
              recruitWriter.getMemberId());
      log.info("participants : {}", participants);

      //then
      assertThat(participants.getCurrentPage()).isEqualTo(1);
      assertThat(participants.getPageSize()).isEqualTo(7);
      assertThat(participants.getTotalPages()).isEqualTo(1);
      assertThat(participants.getTotalElements()).isEqualTo(7);

      List<ParticipantDto> list = participants.getList();
      for (int i = 0; i < list.size(); i++) {
        ParticipantDto participantDto = list.get(i);
        assertThat(participantDto.getRecruitId()).isEqualTo(recruit.getRecruitId());
      }
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void getParticipants_RECRUIT_NOT_FOUND() {
      //given
      MemberEntity recruitWriter = createMember(999L);
      RecruitEntity recruit = createRecruit(10L, recruitWriter);
      List<ParticipantEntity> participantList = new ArrayList<>();

      for (long i = 1; i <= 7; i++) {
        MemberEntity memberEntity = createMember(i);
        ParticipantEntity participant = createParticipant(i + 100, memberEntity, recruit);
        participantList.add(participant);
      }

      int page = 2;

      when(recruitRepository.findById(9L))
          .thenReturn(Optional.empty());
      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
                  participantService
                      .getParticipants(9L, page, recruitWriter.getMemberId()),
              RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

  }


  @Nested
  @DisplayName("모집 글에 참여 신청 - joinRecruit")
  class JoinRecruit {

    @Test
    @DisplayName("정상 - 신청 성공")
    void joinRecruit_JOIN_RECRUIT_COMPLETED() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);

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
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, times(1)).save(new ParticipantEntity(member, recruit));

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_COMPLETE);

    }

    @Test
    @DisplayName("정상 - 신청 진행중인 상태")
    void joinRecruit_JOIN_RECRUIT_ING() {
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
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, never()).save(any(ParticipantEntity.class));
      verify(participantRepository, times(1))
          .findByMemberAndRecruitAndStatus(member, recruit, ParticipantStatus.ING);

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ING);
    }

    @Test
    @DisplayName("정상 - 이미 신청이 수락 된 상태")
    void joinRecruit_JOIN_RECRUIT_ALREADY_CONFIRMED() {
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
      ParticipateResponse response =
          participantService.joinRecruit(member.getMemberId(), recruit.getRecruitId());

      //then
      verify(participantRepository, never()).save(any(ParticipantEntity.class));
      verify(participantRepository, times(1))
          .findByMemberAndRecruitAndStatus(member, recruit, ParticipantStatus.ING);
      verify(participantRepository, times(1))
          .findByMemberAndRecruitAndStatus(member, recruit, ParticipantStatus.ACCEPTED);

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ALREADY_ACCEPTED);
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void joinRecruit_MEMBER_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
              participantService.joinRecruit(
                  member.getMemberId(), recruit.getRecruitId()), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void joinRecruit_RECRUIT_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              participantService.joinRecruit(
                  member.getMemberId(), recruit.getRecruitId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("참여 신청 수락/거절")
  class ManageJoinRecruit {

    @Test
    @DisplayName("정상 - 참여 신청 수락, 마감 x")
    void manageJoinRecruit_JOIN_RECRUIT_ACCEPTED() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);
      recruit.setCurrentCount(2);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(true, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          applicant, recruit, ParticipantStatus.ING))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), writer.getMemberId());

      //then
      verify(participantRepository, times(1)).save(participant);
      verify(recruitRepository, times(1)).save(recruit);

      log.info("recruit : {}", recruit);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED); //confirm()
      assertThat(recruit.getCurrentCount()).isEqualTo(3);//participantAdded()

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ACCEPTED);
    }

    @Test
    @DisplayName("정상 - 참여 신청 수락, 마감 o")
    void manageJoinRecruit_JOIN_RECRUIT_ACCEPTED_Recruit_END() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);
      recruit.setCurrentCount(9);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(true, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          applicant, recruit, ParticipantStatus.ING))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), writer.getMemberId());

      //then
      verify(participantRepository, times(1)).save(participant);
      verify(recruitRepository, times(1)).save(recruit);

      log.info("recruit : {}", recruit);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED); //confirm()
      assertThat(recruit.getCurrentCount()).isEqualTo(10);//participantAdded()
      assertThat(recruit.getRecruitStatus()).isEqualTo(RecruitStatus.END);

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ACCEPTED);
    }

    @Test
    @DisplayName("정상 - 참여 신청 수락, ALMOST_FINISHED로 변경")
    void manageJoinRecruit_JOIN_RECRUIT_ACCEPTED_status_to_ALMOST_FINISHED() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);
      recruit.setCurrentCount(8);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(true, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          applicant, recruit, ParticipantStatus.ING))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), writer.getMemberId());

      //then
      verify(participantRepository, times(1)).save(participant);
      verify(recruitRepository, times(1)).save(recruit);

      log.info("recruit : {}", recruit);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED); //confirm()
      assertThat(recruit.getCurrentCount()).isEqualTo(9);//participantAdded()
      assertThat(recruit.getRecruitStatus()).isEqualTo(RecruitStatus.ALMOST_FINISHED); //statusToAlmostFinished()

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_ACCEPTED);
    }

    @Test
    @DisplayName("정상 : 참여 신청 거절")
    void manageJoinRecruit_JOIN_RECRUIT_REJECTED() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(false, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          applicant, recruit, ParticipantStatus.ING))
          .thenReturn(Optional.of(participant));

      //when
      ParticipantManage.Response response =
          participantService.manageJoinRecruit(
              request, recruit.getRecruitId(), writer.getMemberId());

      //then
      verify(participantRepository, times(1)).save(participant);
      assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.REFUSED);//refuse()

      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getApplicantId()).isEqualTo(applicant.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.JOIN_RECRUIT_REJECTED);
    }

    @Test
    @DisplayName("실패 - RECRUIT_ALREADY_END")
    void manageJoinRecruit_RECRUIT_ALREADY_END() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);
      recruit.setRecruitStatus(RecruitStatus.END);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(false, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              participantService.manageJoinRecruit(
                  request, recruit.getRecruitId(), writer.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_ALREADY_END);
    }

    @Test
    @DisplayName("실패 - APPLICANT_MEMBER_NOT_FOUND")
    void manageJoinRecruit_APPLICANT_MEMBER_NOT_FOUND() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(false, 3L);

      when(memberRepository.findById(3L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
              participantService.manageJoinRecruit(
                  request, recruit.getRecruitId(), writer.getMemberId()), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.APPLICANT_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void manageJoinRecruit_RECRUIT_NOT_FOUND() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);

      //given
      MemberEntity applicant = createMember(2L);
      ParticipantEntity participant = createParticipant(100L, applicant, recruit);
      participant.setStatus(ParticipantStatus.ING);

      ParticipantManage.Request request =
          new Request(false, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              participantService.manageJoinRecruit(
                  request, 11L, writer.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - PARTICIPANT_NOT_FOUND")
    void manageJoinRecruit_PARTICIPANT_NOT_FOUND() {
      MemberEntity writer = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, writer);

      //given
      MemberEntity applicant = createMember(2L);

      ParticipantManage.Request request =
          new Request(false, applicant.getMemberId());

      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(applicant));
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruitAndStatus(
          applicant, recruit, ParticipantStatus.ING))
          .thenReturn(Optional.empty());

      //when
      //then
      ParticipantException exception =
          catchThrowableOfType(() ->
                  participantService.manageJoinRecruit(
                      request, recruit.getRecruitId(), writer.getMemberId()),
              ParticipantException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PARTICIPANT_NOT_FOUND);
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