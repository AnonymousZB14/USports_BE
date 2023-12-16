package com.anonymous.usports.domain.evaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Request;
import com.anonymous.usports.domain.evaluation.dto.EvaluationRegister.Response;
import com.anonymous.usports.domain.evaluation.entity.EvaluationEntity;
import com.anonymous.usports.domain.evaluation.repository.EvaluationRepository;
import com.anonymous.usports.domain.evaluation.service.impl.EvaluationServiceImpl;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sportsskill.entity.SportsSkillEntity;
import com.anonymous.usports.domain.sportsskill.repository.SportsSkillRepository;
import com.anonymous.usports.global.exception.EvaluationException;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.RecruitStatus;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class EvaluationServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private RecruitRepository recruitRepository;
  @Mock
  private EvaluationRepository evaluationRepository;
  @Mock
  private SportsSkillRepository sportsSkillRepository;
  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private EvaluationServiceImpl evaluationService;

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
        .mannerScore(0.0)
        .kindnessScore(0L)
        .passionScore(0L)
        .teamworkScore(0L)
        .evaluationCount(0L)
        .role(Role.USER)
        .profileOpen(true)
        .build();
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

  private SportsEntity createSports(Long id, String sportsName) {
    return new SportsEntity(id, sportsName);
  }

  private ParticipantEntity createParticipant(Long id, MemberEntity member, RecruitEntity recruit) {
    return ParticipantEntity.builder()
        .participantId(id)
        .member(member)
        .recruit(recruit)
        .registeredAt(LocalDateTime.now())
        .build();
  }

  private EvaluationEntity createEvaluation(
      Long id, RecruitEntity recruit, MemberEntity fromMember, MemberEntity toMember) {
    return EvaluationEntity.builder()
        .evaluationId(id)
        .recruit(recruit)
        .fromMember(fromMember)
        .toMember(toMember)
        .kindness(5)
        .passion(5)
        .teamwork(5)
        .sportsScore(5)
        .registeredAt(LocalDateTime.now())
        .build();
  }

  @Nested
  @DisplayName("평가 등록하기")
  class RegisterEvaluation {

    @Test
    @DisplayName("성공 1 : SportsSkill 첫 등록")
    void registerEvaluation_SportsSkill_doesnt_exists() {
      MemberEntity recruitOwnerMember = createMember(3L);
      SportsEntity sports = createSports(99L, "축구");
      RecruitEntity recruit = createRecruit(999L, recruitOwnerMember, sports);

      MemberEntity fromMember = createMember(1L); //평가자
      MemberEntity toMember = createMember(2L); //피 평가자

      //첫번째 멤버의 기록, participant의 evaluate_at을 변경하기 위함
      ParticipantEntity participantOfFromMember = createParticipant(100L, fromMember, recruit);
      ParticipantEntity participantOfToMember = createParticipant(101L, toMember, recruit); //해당 participant는 사용되지는 않음.

      EvaluationEntity evaluation = createEvaluation(1000L, recruit, fromMember, toMember);

      EvaluationRegister.Request request =
          EvaluationRegister.Request.builder()
              .recruitId(999L)
              .toMemberId(2L)
              .kindness(5)
              .passion(5)
              .teamwork(5)
              .sportsScore(5)
              .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(recruitRepository.findById(999L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruit(fromMember, recruit))
          .thenReturn(Optional.of(participantOfFromMember));
      when(participantRepository.findByMemberAndRecruit(toMember, recruit))
          .thenReturn(Optional.of(participantOfToMember));
      when(evaluationRepository.existsByRecruitAndFromMemberAndToMember(recruit, fromMember,
          toMember))
          .thenReturn(false);

      when(evaluationRepository.save(Request.toEntity(request, recruit, fromMember, toMember)))
          .thenReturn(evaluation);

      when(sportsSkillRepository.findByMemberAndSports(toMember, sports))
          .thenReturn(Optional.empty());

      //when
      Response response = evaluationService.registerEvaluation(request, fromMember.getMemberId());

      //then
      verify(sportsSkillRepository, times(1))
          .save(new SportsSkillEntity(toMember, sports, request.getSportsScore()));

      assertThat(response.getEvaluationId()).isEqualTo(evaluation.getEvaluationId());
      assertThat(response.getFromMemberId()).isEqualTo(fromMember.getMemberId());
      assertThat(response.getToMemberId()).isEqualTo(toMember.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.EVALUATION_SUCCEED);

      assertThat(toMember.getKindnessScore()).isEqualTo(5);
      assertThat(toMember.getPassionScore()).isEqualTo(5);
      assertThat(toMember.getTeamworkScore()).isEqualTo(5);
      assertThat(toMember.getMannerScore()).isEqualTo(5);

      assertThat(participantOfFromMember.getEvaluationAt()).isNotNull();
    }

    @Test
    @DisplayName("성공 2 : 기존 SportsSkill update")
    void registerEvaluation_SportsSkill_exists() {
      MemberEntity recruitOwnerMember = createMember(3L);
      SportsEntity sports = createSports(99L, "축구");
      RecruitEntity recruit = createRecruit(999L, recruitOwnerMember, sports);

      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);

      ParticipantEntity participantOfFromMember = createParticipant(100L, fromMember, recruit);
      ParticipantEntity participantOfToMember = createParticipant(101L, toMember, recruit);

      EvaluationEntity evaluation = createEvaluation(1000L, recruit, fromMember, toMember);
      SportsSkillEntity sportsSkill = new SportsSkillEntity(toMember, sports, 5);

      EvaluationRegister.Request request =
          EvaluationRegister.Request.builder()
              .recruitId(999L)
              .toMemberId(2L)
              .kindness(5)
              .passion(5)
              .teamwork(5)
              .sportsScore(5)
              .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(recruitRepository.findById(999L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruit(fromMember, recruit))
          .thenReturn(Optional.of(participantOfFromMember));
      when(participantRepository.findByMemberAndRecruit(toMember, recruit))
          .thenReturn(Optional.of(participantOfToMember));
      when(evaluationRepository.existsByRecruitAndFromMemberAndToMember(recruit, fromMember,
          toMember))
          .thenReturn(false);

      when(evaluationRepository.save(Request.toEntity(request, recruit, fromMember, toMember)))
          .thenReturn(evaluation);

      when(sportsSkillRepository.findByMemberAndSports(toMember, sports))
          .thenReturn(Optional.of(sportsSkill));

      //when
      Response response = evaluationService.registerEvaluation(request, fromMember.getMemberId());

      //then
      verify(sportsSkillRepository, times(1))
          .save(new SportsSkillEntity(toMember, sports, request.getSportsScore()));

      assertThat(response.getEvaluationId()).isEqualTo(evaluation.getEvaluationId());
      assertThat(response.getFromMemberId()).isEqualTo(fromMember.getMemberId());
      assertThat(response.getToMemberId()).isEqualTo(toMember.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.EVALUATION_SUCCEED);

      assertThat(toMember.getKindnessScore()).isEqualTo(5);
      assertThat(toMember.getPassionScore()).isEqualTo(5);
      assertThat(toMember.getTeamworkScore()).isEqualTo(5);
      assertThat(toMember.getMannerScore()).isEqualTo(5);

      assertThat(sportsSkill.getEvaluateCount()).isEqualTo(2);
      assertThat(sportsSkill.getSportsScore()).isEqualTo(10);

      assertThat(participantOfFromMember.getEvaluationAt()).isNotNull();
    }

    @Test
    @DisplayName("실패 : RECRUIT_NOT_FINISHED")
    void registerEvaluation_RECRUIT_NOT_FINISHED() {
      MemberEntity recruitOwnerMember = createMember(3L);
      SportsEntity sports = createSports(99L, "축구");
      RecruitEntity recruit = createRecruit(999L, recruitOwnerMember, sports);
      recruit.setMeetingDate(LocalDateTime.now().plusHours(1L)); //meetingDate가 지나지 않은 경우를 가정

      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);

      ParticipantEntity participantOfFromMember = createParticipant(100L, fromMember, recruit);
      ParticipantEntity participantOfToMember = createParticipant(101L, toMember, recruit);

      EvaluationEntity evaluation = createEvaluation(1000L, recruit, fromMember, toMember);
      SportsSkillEntity sportsSkill = new SportsSkillEntity(toMember, sports, 5);

      EvaluationRegister.Request request =
          EvaluationRegister.Request.builder()
              .recruitId(999L)
              .toMemberId(2L)
              .kindness(5)
              .passion(5)
              .teamwork(5)
              .sportsScore(5)
              .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(recruitRepository.findById(999L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruit(fromMember, recruit))
          .thenReturn(Optional.of(participantOfFromMember));
      when(participantRepository.findByMemberAndRecruit(toMember, recruit))
          .thenReturn(Optional.of(participantOfToMember));

      //when
      //then
      EvaluationException exception =
          catchThrowableOfType(() ->
                  evaluationService.registerEvaluation(request, fromMember.getMemberId()),
              EvaluationException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FINISHED);
    }

    @Test
    @DisplayName("실패 : EVALUATION_ALREADY_EXISTS")
    void registerEvaluation_EVALUATION_ALREADY_EXISTS() {
      MemberEntity recruitOwnerMember = createMember(3L);
      SportsEntity sports = createSports(99L, "축구");
      RecruitEntity recruit = createRecruit(999L, recruitOwnerMember, sports);
      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);
      ParticipantEntity participantOfFromMember = createParticipant(100L, fromMember, recruit);
      ParticipantEntity participantOfToMember = createParticipant(101L, toMember, recruit);
      EvaluationEntity evaluation = createEvaluation(1000L, recruit, fromMember, toMember);
      SportsSkillEntity sportsSkill = new SportsSkillEntity(toMember, sports, 5);
      EvaluationRegister.Request request =
          EvaluationRegister.Request.builder()
              .recruitId(999L)
              .toMemberId(2L)
              .kindness(5)
              .passion(5)
              .teamwork(5)
              .sportsScore(5)
              .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(recruitRepository.findById(999L))
          .thenReturn(Optional.of(recruit));
      when(participantRepository.findByMemberAndRecruit(fromMember, recruit))
          .thenReturn(Optional.of(participantOfFromMember));
      when(participantRepository.findByMemberAndRecruit(toMember, recruit))
          .thenReturn(Optional.of(participantOfToMember));
      when(evaluationRepository.existsByRecruitAndFromMemberAndToMember(recruit, fromMember,
          toMember))
          .thenReturn(true);

      //when
      //then
      EvaluationException exception =
          catchThrowableOfType(() ->
                  evaluationService.registerEvaluation(request, fromMember.getMemberId()),
              EvaluationException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EVALUATION_ALREADY_EXISTS);
    }

  }

}