package com.anonymous.usports.domain.comment.service;

import com.anonymous.usports.domain.comment.dto.CommentDto;
import com.anonymous.usports.domain.comment.dto.CommentRegister.Request;
import com.anonymous.usports.domain.comment.dto.CommentUpdate;

public interface CommentService {

  CommentDto registerComment(Long recordId, Long parentId, Request request, Long loginMemberId);

  CommentDto updateComment(Long recordId, Long commentId, CommentUpdate.Request request, Long loginMemberId);

  CommentDto deleteComment(Long recordId, Long commentId, Long loginMemberId);
}
