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

  private int currentCount; //현재 인원
  private int totalCount; //모집 총 인원

  private List<ParticipantInfo> ingList; //신청중 리스트
  private List<ParticipantInfo> acceptedList; //수락된 리스트

}
