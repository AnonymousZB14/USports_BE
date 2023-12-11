package com.anonymous.usports.domain.comment.dto;

import com.anonymous.usports.global.constant.ResponseConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentDelete {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {

    private Long commentId;
    private String message;

    public Response (CommentDto commentDto){
      this.commentId = commentDto.getCommentId();
      this.message = ResponseConstant.DELETE_COMMENT;
    }
  }

}
