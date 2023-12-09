package com.anonymous.usports.domain.comment.controller;

import com.anonymous.usports.domain.comment.dto.CommentDelete;
import com.anonymous.usports.domain.comment.dto.CommentDto;
import com.anonymous.usports.domain.comment.dto.CommentRegister;
import com.anonymous.usports.domain.comment.dto.CommentRegister.Response;
import com.anonymous.usports.domain.comment.dto.CommentUpdate;
import com.anonymous.usports.domain.comment.service.CommentService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @ApiOperation("댓글 작성")
  @PostMapping("/record/{recordId}/comment")
  public ResponseEntity<CommentRegister.Response> registerComment(
      @PathVariable Long recordId,
      @RequestParam(value = "parent", required = false) Long parentId,
      @RequestBody CommentRegister.Request request,
      @AuthenticationPrincipal MemberDto loginMember
  ) {
    CommentDto commentDto = commentService.registerComment(recordId, parentId, request, loginMember.getMemberId());
    return ResponseEntity.ok(new Response(commentDto));
  }

  @ApiOperation("댓글 수정")
  @PutMapping("/record/{recordId}/comment/{commentId}")
  public ResponseEntity<CommentUpdate.Response> updateComment(
      @PathVariable Long recordId,
      @PathVariable Long commentId,
      @RequestBody CommentUpdate.Request request,
      @AuthenticationPrincipal MemberDto loginMember
  ) {
    CommentDto commentDto = commentService.updateComment(recordId,commentId,request, loginMember.getMemberId());
    return ResponseEntity.ok(new CommentUpdate.Response(commentDto));
  }

  @ApiOperation("댓글 삭제")
  @DeleteMapping("/record/{recordId}/comment/{commentId}")
  public ResponseEntity<CommentDelete.Response> deleteComment(
      @PathVariable Long recordId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal MemberDto loginMember
  ) {
    CommentDto commentDto = commentService.deleteComment(recordId, commentId, loginMember.getMemberId());
    return ResponseEntity.ok(new CommentDelete.Response(commentDto));
  }

}
