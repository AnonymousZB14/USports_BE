package com.anonymous.usports.websocket.dto;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.websocket.entity.ChattingEntity;
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
public class ChatMessageListDto {

  private int currentPage;
  private int currentElements;
  private int pageSize;
  private int totalPages;
  private int totalElements;

  private List<ChatMessageDto> list;

  public ChatMessageListDto(Page<ChattingEntity> chattingEntityPage , List<MemberEntity> participantList) {
    this.currentPage = chattingEntityPage.getNumber()+1;
    this.currentElements = chattingEntityPage.getNumberOfElements();
    this.pageSize = chattingEntityPage.getSize();
    this.totalPages = chattingEntityPage.getTotalPages();
    this.totalElements = (int) chattingEntityPage.getTotalElements();
    this.list = chattingEntityPage.getContent().stream()
        .map(chattingEntity -> toChatMessageDto(chattingEntity,participantList))
        .collect(Collectors.toList());
  }

  public ChatMessageDto toChatMessageDto(ChattingEntity chattingEntity, List<MemberEntity> participantList) {
    MemberEntity member = findMemberById(chattingEntity.getMemberId(), participantList);

    return ChatMessageDto.builder()
        .chatRoomId(chattingEntity.getChatRoomId())
        .userId(member.getMemberId())
        .user(member.getAccountName())
        .time(chattingEntity.getCreatedAt())
        .imageAddress(member.getProfileImage())
        .content(chattingEntity.getContent())
        .type(chattingEntity.getType())
        .build();
  }
  private MemberEntity findMemberById(Long memberId, List<MemberEntity> participantList) {
    return participantList.stream()
        .filter(member -> memberId.equals(member.getMemberId()))
        .findFirst()
        .orElse(null);
  }

}
