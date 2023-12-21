package com.anonymous.usports.domain.customerservice.dto;

import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.global.type.CsStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsDto {

  private Long csId;

  private MemberDto memberDto;

  private String title;

  private String content;

  private CsStatus csStatus;

  private LocalDateTime registeredAt;

  private LocalDateTime updatedAt;

  public static CsDto fromEntity(CsEntity cs) {
    return CsDto.builder()
        .csId(cs.getCsId())
        .memberDto(MemberDto.fromEntity(cs.getMemberEntity()))
        .title(cs.getTitle())
        .content(cs.getContent())
        .registeredAt(cs.getRegisteredAt())
        .updatedAt(cs.getUpdatedAt())
        .build();
  }

}
