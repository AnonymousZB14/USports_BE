package com.anonymous.usports.domain.recordlike.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.recordlike.dto.RecordLikeDto;
import com.anonymous.usports.domain.recordlike.entity.RecordLikeEntity;
import com.anonymous.usports.domain.recordlike.repository.RecordLikeRepository;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordLikeServiceImplTest {

  @Mock
  private RecordRepository recordRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private RecordLikeRepository recordLikeRepository;

  @InjectMocks
  private RecordLikeServiceImpl recordLikeService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("testAccountName" + id)
        .name("testName" + id)
        .email("test@test" + id + ".com")
        .password("password" + id)
        .phoneNumber("010-1111-111" + id)
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();
  }
  private SportsEntity createSports(Long id,String sportsName) {
    return SportsEntity.builder()
        .sportsId(id)
        .sportsName(sportsName)
        .build();
  }

  private RecordEntity createRecord(Long id, MemberEntity member, SportsEntity sports) {
    List<String> images = new ArrayList<>();
    for(int i=0; i<3;i++){
      images.add("http://이미지주소"+id+".com");
    }
    return RecordEntity.builder()
        .recordId(id)
        .member(member)
        .sports(sports)
        .recordContent("테스트 기록 내용"+id)
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .countComment(0L)
        .imageAddress(images)
        .countRecordLikes(0L)
        .build();
  }
  private RecordLikeEntity createRecordLike(Long id, MemberEntity member, RecordEntity record) {
    return RecordLikeEntity.builder()
        .recordLikeId(id)
        .record(record)
        .member(member)
        .build();
  }

  @Nested
  @DisplayName("좋아요 신청/취소")
  class switchLike {

    @Test
    @DisplayName("성공 - 좋아요 신청")
    void success_LikeOn(){
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L,otherMember,sports);
      RecordLikeEntity recordLike = createRecordLike(1000L,member,record);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(recordLikeRepository.findByRecordAndMember(record,member))
          .thenReturn(Optional.empty());
      when(recordLikeRepository.save(new RecordLikeEntity()))
          .thenReturn(recordLike);

      RecordLikeDto response = recordLikeService.switchLikeOrCancel(record.getRecordId(), member.getMemberId());

      verify(recordLikeRepository,times(1)).save(new RecordLikeEntity());
      verify(recordRepository,times(1)).save(record);

      assertEquals(response.getRecordId(), record.getRecordId());
      assertEquals(response.getMemberId(), member.getMemberId());
      assertEquals(response.getMessage(), ResponseConstant.LIKE_RECORD);
    }

    @Test
    @DisplayName("성공 - 좋아요 취소")
    void success_LikeOff(){
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L,otherMember,sports);
      RecordLikeEntity recordLike = createRecordLike(1000L,member,record);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(recordLikeRepository.findByRecordAndMember(record,member))
          .thenReturn(Optional.of(recordLike));

      RecordLikeDto response = recordLikeService.switchLikeOrCancel(record.getRecordId(), member.getMemberId());

      verify(recordLikeRepository,times(1)).delete(recordLike);
      verify(recordRepository,times(1)).save(record);

      assertEquals(response.getRecordId(), record.getRecordId());
      assertEquals(response.getMemberId(), member.getMemberId());
      assertEquals(response.getMessage(), ResponseConstant.CANCEL_LIKE_RECORD);
    }
    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void fail_MemberNotFound(){
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L,otherMember,sports);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(()->
              recordLikeService.switchLikeOrCancel(record.getRecordId(),member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }
    @Test
    @DisplayName("실패 - RECORD_NOT_FOUND")
    void fail_RecordNotFound(){
      MemberEntity member = createMember(1L);
      MemberEntity otherMember = createMember(2L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L,otherMember,sports);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.empty());

      RecordException exception =
          catchThrowableOfType(()->
                  recordLikeService.switchLikeOrCancel(record.getRecordId(),member.getMemberId()),
              RecordException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.RECORD_NOT_FOUND);
    }
    @Test
    @DisplayName("실패 - SELF_LIKE_NOT_ALLOWED")
    void fail_SelfLikeNotAllowed(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L,member,sports);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));

      RecordException exception =
          catchThrowableOfType(()->
                  recordLikeService.switchLikeOrCancel(record.getRecordId(),member.getMemberId()),
              RecordException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.SELF_LIKE_NOT_ALLOWED);
    }
  }
}