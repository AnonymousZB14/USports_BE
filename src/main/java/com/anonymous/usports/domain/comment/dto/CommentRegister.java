package com.anonymous.usports.domain.comment.dto;

import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentRegister {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {
    private String content;

    public static CommentEntity toEntity(CommentRegister.Request request, MemberEntity member, RecordEntity record, Long parent) {
      return CommentEntity.builder()
          .member(member)
          .record(record)
          .content(request.getContent())
          .parentId(parent)
          .build();
    }
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  public static class Response {
    private Long commentId;
    private String message;

    public Response(CommentDto commentDto){
      this.commentId = commentDto.getCommentId();
      this.message = ResponseConstant.CREATE_COMMENT;
    }
  }

}
