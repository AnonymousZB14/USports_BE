package com.anonymous.usports.domain.comment.dto;

import com.anonymous.usports.domain.comment.entity.CommentEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentDto {

  private Long commentId;

  private Long memberId;

  private String accountName;

  private String name;

  private String profileImage;

  private Long recordId;

  private String content;

  private LocalDateTime registerAt;

  private LocalDateTime updatedAt;

  private Long parentId;

  public static CommentDto fromEntity(CommentEntity commentEntity) {
    return CommentDto.builder()
        .commentId(commentEntity.getCommentId())
        .memberId(commentEntity.getMember().getMemberId())
        .accountName(commentEntity.getMember().getAccountName())
        .name(commentEntity.getMember().getName())
        .profileImage(commentEntity.getMember().getProfileImage())
        .recordId(commentEntity.getRecord().getRecordId())
        .content(commentEntity.getContent())
        .registerAt(commentEntity.getRegisteredAt())
        .updatedAt(commentEntity.getUpdatedAt())
        .parentId(commentEntity.getParentId())
        .build();
  }

}
