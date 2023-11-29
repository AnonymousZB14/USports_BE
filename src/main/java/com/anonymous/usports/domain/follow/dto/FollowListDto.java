package com.anonymous.usports.domain.follow.dto;

import com.anonymous.usports.domain.follow.entity.FollowEntity;
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
public class FollowListDto {

  private int currentPage;
  private int pageSize;
  private int totalPages;
  private int totalElements;

  private List<FollowMemberDetail> list;

  public static FollowListDto fromEntityPage(Page<FollowEntity> followEntityPage) {
    List<FollowMemberDetail> followMemberDetailList = followEntityPage.getContent().stream()
        .map(FollowMemberDetail::fromEntity)
        .collect(Collectors.toList());
    return FollowListDto.builder()
        .currentPage(followEntityPage.getNumber() + 1)
        .pageSize(followEntityPage.getSize())
        .totalPages(followEntityPage.getTotalPages())
        .totalElements((int) followEntityPage.getTotalElements())
        .list(followMemberDetailList)
        .build();
  }

}
