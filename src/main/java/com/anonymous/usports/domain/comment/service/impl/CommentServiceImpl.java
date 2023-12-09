package com.anonymous.usports.domain.comment.service.impl;

import com.anonymous.usports.domain.comment.dto.CommentDto;
import com.anonymous.usports.domain.comment.dto.CommentRegister.Request;
import com.anonymous.usports.domain.comment.dto.CommentUpdate;
import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.comment.repository.CommentRepository;
import com.anonymous.usports.domain.comment.service.CommentService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.domain.record.repository.RecordRepository;
import com.anonymous.usports.global.exception.CommentException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.RecordException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final MemberRepository memberRepository;
  private final RecordRepository recordRepository;
  private final CommentRepository commentRepository;

  /**
   * 댓글 작성 메서드
   *
   * @param recordId 기록 Id
   * @param parentId parentId null이면 일반 댓글, 값이 있을 경우 해당 댓글의 대댓글
   * @param request 댓글 content
   * @param loginMemberId 로그인 한 회원 Id
   * @return CommentDto로 반환
   */
  @Override
  @Transactional
  public CommentDto registerComment(Long recordId, Long parentId, Request request, Long loginMemberId) {
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(()->new RecordException(ErrorCode.RECORD_NOT_FOUND));
    MemberEntity loginMember = memberRepository.findById(loginMemberId)
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    boolean existsComment = commentRepository.existsByCommentId(parentId);
    if(parentId!=null&&!existsComment){
      throw new CommentException(ErrorCode.COMMENT_NOT_FOUND);
    }
    CommentEntity comment = commentRepository.save(Request.toEntity(request,loginMember,record,parentId));
    record.setCountComment(record.getCountComment()+1);
    recordRepository.save(record);
    return CommentDto.fromEntity(comment);
  }

  /**
   * 댓글 수정 메서드
   *
   * @param recordId
   * @param commentId
   * @param request
   * @param loginMemberId
   * @return
   */
  @Override
  @Transactional
  public CommentDto updateComment(Long recordId, Long commentId, CommentUpdate.Request request,
      Long loginMemberId) {
    recordRepository.findById(recordId)
        .orElseThrow(()->new RecordException(ErrorCode.RECORD_NOT_FOUND));
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(()->new CommentException(ErrorCode.COMMENT_NOT_FOUND));
    validateMemberAndAuthority(comment,loginMemberId);
    List<CommentEntity> replies = commentRepository.findAllByParentId(commentId);
    if (!replies.isEmpty()) {
      throw new CommentException(ErrorCode.CANNOT_EDIT_PARENT_COMMENT);
    }
    CommentEntity updatedComment = commentRepository.save(CommentUpdate.Request.toEntity(request,comment));
    return CommentDto.fromEntity(updatedComment);
  }

  @Override
  @Transactional
  public CommentDto deleteComment(Long recordId, Long commentId, Long loginMemberId) {
    RecordEntity record = recordRepository.findById(recordId)
        .orElseThrow(()->new RecordException(ErrorCode.RECORD_NOT_FOUND));
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(()->new CommentException(ErrorCode.COMMENT_NOT_FOUND));
    validateMemberAndAuthority(comment, loginMemberId);
    List<CommentEntity> replies = commentRepository.findAllByParentId(commentId);
    if (!replies.isEmpty()) {
      commentRepository.deleteAll(replies);
      record.setCountComment(record.getCountComment()-replies.size());
    }
    commentRepository.delete(comment);
    record.setCountComment(record.getCountComment()-1);
    recordRepository.save(record);
    return CommentDto.fromEntity(comment);
  }
  private void validateMemberAndAuthority(CommentEntity comment, Long loginMemberId) {
    memberRepository.findById(loginMemberId)
        .orElseThrow(()->new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    if(!comment.getMember().getMemberId().equals(loginMemberId)){
      throw new MemberException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}
