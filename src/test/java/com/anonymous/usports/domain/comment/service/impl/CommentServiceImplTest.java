package com.anonymous.usports.domain.comment.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.comment.dto.CommentDto;
import com.anonymous.usports.domain.comment.dto.CommentRegister;
import com.anonymous.usports.domain.comment.dto.CommentRegister.Request;
import com.anonymous.usports.domain.comment.dto.CommentUpdate;
import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.comment.repository.CommentRepository;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.exception.CommentException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
class CommentServiceImplTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private RecordRepository recordRepository;
  @Mock
  private CommentRepository commentRepository;
  @InjectMocks
  private CommentServiceImpl commentService;

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
        .build();
  }
  private CommentEntity createComment(Long id,MemberEntity member,RecordEntity record,Long parentId) {
    return CommentEntity.builder()
        .commentId(id)
        .member(member)
        .record(record)
        .content("댓글 테스트입니다.")
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .parentId(parentId)
        .build();
  }


  @Nested
  @DisplayName("댓글 등록")
  class registerComment {

    @Test
    @DisplayName("성공-댓글 등록")
    void success_Comment() {

      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentRegister.Request request = new Request("댓글 테스트입니다.");

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.save(Request.toEntity(request,member,record,null)))
          .thenReturn(comment);

      //when
      CommentDto result =
          commentService.registerComment(record.getRecordId(),null,request,member.getMemberId());

      //then
      verify(commentRepository,times(1)).save(new CommentEntity());
      verify(recordRepository,times(1)).save(record);

      assertEquals(result.getMemberId(),comment.getMember().getMemberId());
      assertEquals(result.getRecordId(),comment.getRecord().getRecordId());
      assertEquals(result.getParentId(),comment.getParentId());
      assertThat(result.getContent()).isEqualTo(comment.getContent());

    }
    @Test
    @DisplayName("성공-대댓글 등록")
    void success_registerReply() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentEntity reply = createComment(1200L,member,record,1000L);
      CommentRegister.Request request = new Request("댓글 테스트입니다.");

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.existsByCommentId(1000L))
          .thenReturn(true);
      when(commentRepository.save(Request.toEntity(request,member,record,comment.getCommentId())))
          .thenReturn(reply);

      //when
      CommentDto result =
          commentService.registerComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId());

      //then
      verify(commentRepository,times(1)).save(new CommentEntity());
      verify(recordRepository,times(1)).save(record);

      assertEquals(result.getMemberId(),reply.getMember().getMemberId());
      assertEquals(result.getRecordId(),reply.getRecord().getRecordId());
      assertEquals(result.getParentId(),reply.getParentId());
      assertThat(result.getContent()).isEqualTo(reply.getContent());

    }
    @Test
    @DisplayName("실패-대댓글 등록할 원댓글 없음")
    void fail_NotExistsComment() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      createComment(1000L,member,record,null);
      createComment(1200L,member,record,1500L);
      CommentRegister.Request request = new Request("댓글 테스트입니다.");

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));

      //when
      //then
      CommentException exception =
          catchThrowableOfType(()->
                  commentService.registerComment(record.getRecordId(),1500L,request,member.getMemberId()),
              CommentException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.COMMENT_NOT_FOUND);

    }
    @Test
    @DisplayName("실패-MEMBER_NOT_FOUND")
    void fail_MemberNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentRegister.Request request = new Request("댓글 테스트입니다.");
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(()->
              commentService.registerComment(record.getRecordId(),null,request,member.getMemberId()),
          MemberException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.MEMBER_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-RECORD_NOT_FOUND")
    void fail_RecordNotFound() {
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentRegister.Request request = new Request("댓글 테스트입니다.");

      when(recordRepository.findById(100L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecordException exception =
          catchThrowableOfType(()->
                  commentService.registerComment(record.getRecordId(),null,request,member.getMemberId()),
              RecordException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.RECORD_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class updateComment {
    private CommentEntity createUpdateComment(CommentEntity comment, CommentUpdate.Request request){
      return CommentEntity.builder()
          .commentId(comment.getCommentId())
          .member(comment.getMember())
          .record(comment.getRecord())
          .registeredAt(comment.getRegisteredAt())
          .updatedAt(LocalDateTime.now())
          .parentId(comment.getParentId())
          .content(request.getContent())
          .build();
    }

    @Test
    @DisplayName("성공-대댓글 없을 때 댓글 수정")
    void success_updateComment(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");
      CommentEntity updateComment = createUpdateComment(comment,request);

      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findAllByParentId(1000L))
          .thenReturn(Collections.emptyList());
      when(commentRepository.save(comment))
          .thenReturn(updateComment);

      //when
      CommentDto result =
          commentService.updateComment
              (record.getRecordId(), comment.getCommentId(), request,member.getMemberId());

      //then
      verify(commentRepository,times(1)).save(updateComment);

      assertEquals(result.getMemberId(),comment.getMember().getMemberId());
      assertNotEquals(result.getContent(),comment.getContent());
//      assertNotEquals(result.getUpdatedAt(),comment.getRegisteredAt());TODO 테스트 상에서 registerdAt과 updatedAt 차이가 없음. 디버깅 시는 차이 발생
      assertEquals(result.getRecordId(),comment.getRecord().getRecordId());
      assertEquals(result.getCommentId(),comment.getCommentId());
    }
    @Test
    @DisplayName("실패-대댓글 있을 때 댓글 수정")
    void fail_updateCommentExistComment(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");
      List<CommentEntity> customRepliesList = Arrays.asList(
          createComment(2000L, member, record, comment.getCommentId()),
          createComment(3000L, member, record, comment.getCommentId())
      );

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));
      when(commentRepository.findAllByParentId(1000L))
          .thenReturn(customRepliesList);

      //when
      //then
      CommentException exception =
          catchThrowableOfType(()->
                  commentService.updateComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId()),
              CommentException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.CANNOT_EDIT_PARENT_COMMENT);
    }
    @Test
    @DisplayName("실패-RECORD_NOT_FOUND")
    void fail_RecordNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");

      when(recordRepository.findById(100L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecordException exception =
          catchThrowableOfType(()->
                  commentService.updateComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId()),
              RecordException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.RECORD_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-COMMENT_NOT_FOUND")
    void fail_CommentNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");

      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.empty());

      //when
      //then
      CommentException exception =
          catchThrowableOfType(()->
                  commentService.updateComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId()),
              CommentException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.COMMENT_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-MEMBER_NOT_FOUND")
    void fail_MemberNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");


      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));

      //when
      //then
      MemberException exception =
          catchThrowableOfType(()->
                  commentService.updateComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.MEMBER_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-NO_AUTHORITY_ERROR")
    void fail_NoAuthorityError(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      MemberEntity otherMember = createMember(2L);
      CommentEntity comment = createComment(1000L,otherMember,record,null);
      CommentUpdate.Request request = new CommentUpdate.Request("댓글 수정 테스트입니다.");

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));

      //when
      //then
      MemberException exception =
          catchThrowableOfType(()->
                  commentService.updateComment(record.getRecordId(), comment.getCommentId(), request,member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.NO_AUTHORITY_ERROR);
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class deleteComment {
    @Test
    @DisplayName("성공-댓글 삭제")
    void success_deleteComment(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));
      when(commentRepository.findAllByParentId(1000L))
          .thenReturn(Collections.emptyList());

      //when
      CommentDto result =
          commentService.deleteComment
              (record.getRecordId(), comment.getCommentId(),member.getMemberId());

      //then
      verify(commentRepository,times(1)).delete(comment);

      assertEquals(result.getCommentId(), comment.getCommentId());
    }
    @Test
    @DisplayName("성공-대댓글이 달렸을 때 댓글 삭제")
    void success_deleteCommentExistReply(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      MemberEntity otherMember = createMember(2L);
      CommentEntity comment = createComment(1000L,member,record,null);
      List<CommentEntity> customRepliesList = Arrays.asList(
          createComment(2000L, otherMember, record, comment.getCommentId()),
          createComment(3000L, member, record, comment.getCommentId())
      );

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));
      when(commentRepository.findAllByParentId(1000L))
          .thenReturn(customRepliesList);

      //when
      CommentDto result =
          commentService.deleteComment
              (record.getRecordId(), comment.getCommentId(),member.getMemberId());

      //then
      verify(commentRepository,times(1)).delete(comment);
      verify(commentRepository,times(1)).deleteAll(customRepliesList);

      assertEquals(result.getCommentId(), comment.getCommentId());
    }
    @Test
    @DisplayName("실패-RECORD_NOT_FOUND")
    void fail_RecordNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);

      when(recordRepository.findById(100L))
          .thenReturn(Optional.empty());

      //when
      //then
      RecordException exception =
          catchThrowableOfType(()->
                  commentService.deleteComment(record.getRecordId(), comment.getCommentId(),member.getMemberId()),
              RecordException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.RECORD_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-COMMENT_NOT_FOUND")
    void fail_CommentNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);

      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.empty());

      //when
      //then
      CommentException exception =
          catchThrowableOfType(()->
                  commentService.deleteComment(record.getRecordId(), comment.getCommentId(),member.getMemberId()),
              CommentException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.COMMENT_NOT_FOUND);
    }
    @Test
    @DisplayName("실패-MEMBER_NOT_FOUND")
    void fail_MemberNotFound(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      CommentEntity comment = createComment(1000L,member,record,null);

      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(()->
                  commentService.deleteComment(record.getRecordId(), comment.getCommentId(),member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패-NO_AUTHORITY_ERROR")
    void fail_NO_AUTHORITY_ERROR(){
      MemberEntity member = createMember(1L);
      SportsEntity sports = createSports(10L,"축구");
      RecordEntity record = createRecord(100L, member, sports);

      //given
      MemberEntity otherMember = createMember(2L);
      CommentEntity comment = createComment(1000L,otherMember,record,null);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(recordRepository.findById(100L))
          .thenReturn(Optional.of(record));
      when(commentRepository.findById(1000L))
          .thenReturn(Optional.of(comment));

      //when
      //then
      MemberException exception =
          catchThrowableOfType(()->
                  commentService.deleteComment(record.getRecordId(), comment.getCommentId(),member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(),ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}