package com.anonymous.usports.domain.record.dto;

import com.anonymous.usports.domain.comment.dto.CommentDto;
import com.anonymous.usports.domain.comment.entity.CommentEntity;
import com.anonymous.usports.domain.record.entity.RecordEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDto {

  private Long recordId;

  private Long memberId;

  private Long sportsId;

  private String accountName;

  private String name;

  private String recordContent;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  private Long countComment;

  private String profileImage;

  private List<String> imageAddressList;

  private List<String> thumbnailAddressList;

  private List<CommentDto> commentList;

  private Long countRecordLike;


  public static RecordDto fromEntity(RecordEntity recordEntity) {
    return RecordDto.builder()
        .recordId(recordEntity.getRecordId())
        .memberId(recordEntity.getMember().getMemberId())
        .sportsId(recordEntity.getSports().getSportsId())
        .accountName(recordEntity.getMember().getAccountName())
        .name(recordEntity.getMember().getName())
        .profileImage(recordEntity.getMember().getProfileImage())
        .recordContent(recordEntity.getRecordContent())
        .registeredAt(recordEntity.getRegisteredAt())
        .updatedAt(recordEntity.getUpdatedAt())
        .countComment(recordEntity.getCountComment())
        .imageAddressList(recordEntity.getImageAddress())
        .thumbnailAddressList(recordEntity.getThumbnailAddress())
        .countRecordLike(recordEntity.getCountRecordLikes())
        .build();
  }

  public static RecordDto fromEntityIncludeComment(RecordEntity recordEntity, Page<CommentEntity> commentList) {
    return RecordDto.builder()
        .recordId(recordEntity.getRecordId())
        .memberId(recordEntity.getMember().getMemberId())
        .sportsId(recordEntity.getSports().getSportsId())
        .accountName(recordEntity.getMember().getAccountName())
        .name(recordEntity.getMember().getName())
        .profileImage(recordEntity.getMember().getProfileImage())
        .recordContent(recordEntity.getRecordContent())
        .registeredAt(recordEntity.getRegisteredAt())
        .updatedAt(recordEntity.getUpdatedAt())
        .countComment(recordEntity.getCountComment())
        .imageAddressList(recordEntity.getImageAddress())
        .commentList(commentList.getContent().stream().map(CommentDto::fromEntity).collect(
            Collectors.toList()))
        .countRecordLike(recordEntity.getCountRecordLikes())
        .build();
  }
}
