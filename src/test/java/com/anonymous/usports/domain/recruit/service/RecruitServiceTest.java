package com.anonymous.usports.domain.recruit.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import com.anonymous.usports.domain.participant.repository.ParticipantRepository;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitEndResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister.Request;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;
import com.anonymous.usports.domain.recruit.dto.RecruitResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.entity.RecruitEntity;
import com.anonymous.usports.domain.recruit.repository.RecruitRepository;
import com.anonymous.usports.domain.recruit.service.impl.RecruitServiceImpl;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.exception.RecruitException;
import com.anonymous.usports.global.exception.SportsException;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
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
  @Mock
  private ParticipantRepository participantRepository;


  @InjectMocks
  private RecruitServiceImpl recruitService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("accountName" + id)
        .name("name" + id)
        .email("test@test.com")
        .password("password" + id)
        .phoneNumber("010-1111-2 222")
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
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
  private SportsEntity createSports(Long id) {
    return new SportsEntity(id, "football");
  }

  private SportsEntity createSports(Long id, String sportsName) {
    return new SportsEntity(id, sportsName);
  }

  @Nested
  @DisplayName("Recruit 등록")
  class RegisterRecruit {

    private RecruitRegister.Request createRegisterRequest(RecruitEntity recruit) {
      return RecruitRegister.Request.builder()
          .sportsId(recruit.getSports().getSportsId())
          .title(recruit.getTitle())
          .content(recruit.getContent())
          .placeName(recruit.getPlaceName())
          .lat(recruit.getLat())
          .lnt(recruit.getLnt())
          .cost(recruit.getCost())
          .recruitCount(recruit.getRecruitCount())
          .meetingDate(recruit.getMeetingDate())
          .gender(recruit.getGender())
          .gradeFrom(recruit.getGradeFrom())
          .gradeTo(recruit.getGradeTo())
          .build();
    }

    @Test
    @DisplayName("성공")
    void registerRecruit() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      RecruitRegister.Request request = createRegisterRequest(recruit);
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(1000L))
          .thenReturn(Optional.of(sports));

      when(recruitRepository.save(Request.toEntity(request, member, sports)))
          .thenReturn(recruit);

      //when
      RecruitDto result = recruitService.registerRecruit(request, member.getMemberId());

      //then
      assertThat(result.getSportsId()).isEqualTo(sports.getSportsId());
      assertThat(result.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(result.getRecruitStatus()).isEqualTo(RecruitStatus.RECRUITING);
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void registerRecruit_MEMBER_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      RecruitRegister.Request request = createRegisterRequest(recruit);

      when(memberRepository.findById(2L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
              recruitService.registerRecruit(request, 2L), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - SPORTS_NOT_FOUND")
    void registerRecruit_SPORTS_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      RecruitRegister.Request request = createRegisterRequest(recruit);
      request.setSportsId(1001L);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(sportsRepository.findById(1001L))
          .thenReturn(Optional.empty());

      //when
      //then
      SportsException exception =
          catchThrowableOfType(() ->
              recruitService.registerRecruit(request, member.getMemberId()), SportsException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SPORTS_NOT_FOUND);
    }

  }

  @Nested
  @DisplayName("Recruit 한 건 조회")
  class GetRecruit {

    @Test
    @DisplayName("성공")
    void getRecruit() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));

      //when
      RecruitResponse result = recruitService.getRecruit(10L);

      //then
      assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void getRecruit_RECRUIT_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      //given
      when(recruitRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              recruitService.getRecruit(11L), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Recruit 수정")
  class UpdateRecruit {

    private RecruitUpdate.Request createUpdateRequest(RecruitEntity recruit) {
      String UPDATE = "update";
      return RecruitUpdate.Request.builder()
          .sportsId(recruit.getSports().getSportsId())
          .title(recruit.getTitle() + UPDATE)
          .content(recruit.getContent() + UPDATE)
          .placeName(recruit.getPlaceName() + UPDATE)
          .lat("11")
          .lnt("22")
          .cost(recruit.getCost() + 10)
          .recruitCount(recruit.getRecruitCount() + 1)
          .meetingDate(recruit.getMeetingDate())
          .gender(Gender.BOTH)
          .gradeFrom(recruit.getGradeFrom() + 1)
          .gradeTo(recruit.getGradeTo() - 1)
          .build();
    }

    @Test
    @DisplayName("성공")
    void updateRecruit() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      RecruitUpdate.Request request = createUpdateRequest(recruit);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(sportsRepository.findById(1000L))
          .thenReturn(Optional.of(sports));
      when(recruitRepository.save(recruit))
          .thenReturn(recruit);

      //when
      RecruitDto result = recruitService.updateRecruit(request, recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(result.getTitle()).isEqualTo(request.getTitle());
      assertThat(result.getContent()).isEqualTo(request.getContent());
      assertThat(result.getPlaceName()).isEqualTo(request.getPlaceName());
      assertThat(result.getGender()).isEqualTo(request.getGender());
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void updateRecruit_RECRUIT_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      RecruitUpdate.Request request = createUpdateRequest(recruit);

      //given
      when(recruitRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              recruitService.updateRecruit(request, 11L,
                  member.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - NO_AUTHORITY_ERROR")
    void updateRecruit_NO_AUTHORITY_ERROR() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      RecruitUpdate.Request request = createUpdateRequest(recruit);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));

      //when
      //then
      MyException exception =
          catchThrowableOfType(() ->
              recruitService.updateRecruit(request, 10L,
                  2L), MyException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_ERROR);
    }

    @Test
    @DisplayName("실패 - SPORTS_NOT_FOUND")
    void updateRecruit_SPORTS_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      RecruitUpdate.Request request = createUpdateRequest(recruit);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(sportsRepository.findById(1000L))
          .thenReturn(Optional.empty());

      //when
      //then
      SportsException exception =
          catchThrowableOfType(() ->
              recruitService.updateRecruit(request, 10L,
                  member.getMemberId()), SportsException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SPORTS_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Recruit 삭제")
  class DeleteRecruit {

    @Test
    @DisplayName("성공")
    void deleteRecruit() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));

      //when
      RecruitDto result = recruitService.deleteRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      verify(recruitRepository, times(1)).delete(recruit);
      assertThat(result.getRecruitId()).isEqualTo(recruit.getRecruitId());
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void deleteRecruit_RECRUIT_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      when(recruitRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              recruitService.deleteRecruit(11L,
                  member.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - NO_AUTHORITY_ERROR")
    void deleteRecruit_NO_AUTHORITY_ERROR() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));

      //when
      //then
      MyException exception =
          catchThrowableOfType(() ->
              recruitService.deleteRecruit(10L,
                  2L), MyException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }


  @Nested
  @DisplayName("모집 글 마감 / 마감 취소")
  class EndRecruit {

    @Test
    @DisplayName("RECRUITING -> 마감")
    void endRecruit_RECRUITING_TO_END() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      recruit.setRecruitStatus(RecruitStatus.RECRUITING);


      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(participantRepository.findAllByRecruitAndStatus(recruit, ParticipantStatus.ING))
          .thenReturn(new ArrayList<>());

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_COMPLETE);
    }

    @Test
    @DisplayName("ALMOST_FINISHED -> 마감")
    void endRecruit() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      recruit.setRecruitStatus(RecruitStatus.ALMOST_END);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(1L))
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
    void endRecruit_END_TO_RECRUITING() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      recruit.setRecruitStatus(RecruitStatus.END);
      recruit.setCurrentCount(3);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(1L))
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
    void endRecruit_END_TO_ALMOST_FINISHED() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);
      recruit.setRecruitStatus(RecruitStatus.END);
      recruit.setCurrentCount(8);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));

      //when
      RecruitEndResponse response = recruitService.endRecruit(recruit.getRecruitId(),
          member.getMemberId());

      //then
      assertThat(response.getRecruitId()).isEqualTo(recruit.getRecruitId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.END_RECRUIT_CANCELED);
      assertThat(recruit.getRecruitStatus()).isEqualTo(RecruitStatus.ALMOST_END);
    }

    @Test
    @DisplayName("실패 - RECRUIT_NOT_FOUND")
    void endRecruit_RECRUIT_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      when(recruitRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecruitException exception =
          catchThrowableOfType(() ->
              recruitService.endRecruit(11L,
                  member.getMemberId()), RecruitException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RECRUIT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void endRecruit_MEMBER_NOT_FOUND() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
              recruitService.endRecruit(recruit.getRecruitId(),
                  2L), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - NO_AUTHORITY_ERROR")
    void endRecruit_NO_AUTHORITY_ERROR() {
      SportsEntity sports = createSports(1000L);
      MemberEntity member = createMember(1L);
      RecruitEntity recruit = createRecruit(10L, member, sports);

      MemberEntity loginMember = createMember(2L);
      //given
      when(recruitRepository.findById(10L))
          .thenReturn(Optional.of(recruit));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(member));

      //when
      //then
      MyException exception =
          catchThrowableOfType(() ->
              recruitService.endRecruit(recruit.getRecruitId(),
                  2L), MyException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }

  @Nested
  @DisplayName("Recruit 필터링 검색")
  class GetRecruitsByConditions{
    @Test
    @DisplayName("성공")
    void getRecruitsByConditions(){
      MemberEntity memberEntity = createMember(1L);
      SportsEntity football = createSports(1000L, "축구");
      List<RecruitEntity> recruitList = new ArrayList<>();

      int page = 1;
      String search = "title";
      String region = "대구";
      String footballString = "축구";
      Gender gender = Gender.MALE;
      boolean closeInclude = false;

      for(long i = 101; i <= 108; i++){
        RecruitEntity recruit = createRecruit(i, memberEntity, football);
        recruit.setRegion(region);
        recruit.setGender(gender);
        recruitList.add(recruit);
      }
      //given
      when(sportsRepository.findBySportsName(footballString))
          .thenReturn(Optional.of(football));

      when(recruitRepository.findAllByConditionNotIncludeEND(
          search, region, football, gender,
          PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT, Sort.by("registeredAt").descending())))
          .thenReturn(new PageImpl<>(recruitList));
      //when
      RecruitListDto result =
          recruitService.getRecruitsByConditions(
              page, search, region, footballString, gender, closeInclude);

      //then
      assertThat(result.getCurrentElements()).isEqualTo(8);
      assertThat(result.getTotalPages()).isEqualTo(1);
      List<RecruitDto> list = result.getList();
      for (RecruitDto r : list){
        assertThat(r.getTitle().contains(search)).isTrue();
        assertThat(r.getRegion()).isEqualTo(region);
        assertThat(r.getSportsId()).isEqualTo(football.getSportsId());
        assertThat(r.getGender()).isEqualTo(Gender.MALE);
        assertThat(r.getRecruitStatus()).isNotEqualTo(RecruitStatus.END);
      }
    }

    @Test
    @DisplayName("실패 - SPORTS_NOT_FOUND")
    void getRecruitsByConditions_SPORTS_NOT_FOUND(){
      MemberEntity memberEntity = createMember(1L);
      SportsEntity football = createSports(1000L, "축구");
      List<RecruitEntity> recruitList = new ArrayList<>();

      int page = 1;
      String search = "title";
      String region = "대구";
      String footballString = "축구";
      Gender gender = Gender.MALE;
      boolean closeInclude = false;

      for(long i = 101; i <= 108; i++){
        RecruitEntity recruit = createRecruit(i, memberEntity, football);
        recruit.setRegion(region);
        recruit.setGender(gender);
        recruitList.add(recruit);
      }
      //given
      when(sportsRepository.findBySportsName(footballString))
          .thenReturn(Optional.empty());

      //when
      //then
      SportsException exception =
          catchThrowableOfType(() ->
              recruitService.getRecruitsByConditions(
                  page, search, region, footballString, gender, closeInclude), SportsException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SPORTS_NOT_FOUND);

    }
  }
}