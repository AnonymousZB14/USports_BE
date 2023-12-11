package com.anonymous.usports.domain.comment.dto;

import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentUpdate {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request{
    private String content;

    public static CommentEntity toEntity(CommentUpdate.Request request, CommentEntity comment) {
      return CommentEntity.builder()
          .commentId(comment.getCommentId())
          .member(comment.getMember())
          .record(comment.getRecord())
          .content(request.getContent())
          .registeredAt(comment.getRegisteredAt())
          .updatedAt(comment.getUpdatedAt())
          .parentId(comment.getParentId())
          .build();
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private Long commentId;
    private String message;

    public Response(CommentDto commentDto) {
      this.commentId = commentDto.getCommentId();
      this.message = ResponseConstant.UPDATE_COMMENT;
    }
  }

}
