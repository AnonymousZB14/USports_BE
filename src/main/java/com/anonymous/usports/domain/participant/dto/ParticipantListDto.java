package com.anonymous.usports.domain.participant.dto;

import com.anonymous.usports.domain.participant.entity.ParticipantEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ParticipantListDto {

  private int currentPage; //현재 페이지

  private int pageSize; //페이지 당 데이터 개수

  private int totalPages; // 전체 페이지 수

  private int totalElements; //전체 데이터 개수


  private List<ParticipantDto> list;

  public static ParticipantListDto fromEntityPage(Page<ParticipantEntity> participantEntityPage) {
    return ParticipantListDto.builder()
        .currentPage(participantEntityPage.getNumber() + 1)
        .pageSize(participantEntityPage.getSize())
        .totalPages(participantEntityPage.getTotalPages())
        .totalElements((int) participantEntityPage.getTotalElements())
        .list(participantEntityPage.getContent()
            .stream()
            .map(ParticipantDto::fromEntity)
            .collect(Collectors.toList())
        )
        .build();
  }

}
