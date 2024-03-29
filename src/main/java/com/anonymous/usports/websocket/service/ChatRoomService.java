package com.anonymous.usports.websocket.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatMessageListDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.ChatResponses.ChatEnterDto;
import com.anonymous.usports.websocket.dto.ChatResponses.ChatInviteDto;
import com.anonymous.usports.websocket.dto.ChatResponses.CreateDMDto;
import com.anonymous.usports.websocket.dto.ChatResponses.CreateRecruitChat;
import java.util.List;

public interface ChatRoomService {

  ChatEnterDto.Response enterChatRoom(Long chatRoomId, MemberDto memberDto);

    List<ChatPartakeDto> getChatRoomList(MemberDto memberDto);

    /**
     * 채팅방을 만들 때에, 이미 방이 있으면 만들면 안 된다
     */
    CreateDMDto.Response createChatRoom(CreateDMDto.Request request, MemberDto memberDto);

    CreateRecruitChat.Response createRecruitChat(CreateRecruitChat.Request request, MemberDto memberDto);

    ChatInviteDto.Response inviteMemberToRecruitChat(ChatInviteDto.Request request, MemberDto memberDto);

    // DM에서는 한명이라도 채팅방을 나가게 되면, 채팅방은 사라진다
    String exitChat(Long chatRoomId, MemberDto memberDto);

  // 채팅방에서 채팅 내역 가져오기
  ChatMessageListDto getMessageList(Long chatRoomId, MemberDto memberDto, int page);

  // 초대 가능 리스트(모임 수락 상태이면서 채팅방에 없는 사람) 출력
  List<MemberDto> getListToInvite(Long chatRoomId, MemberDto memberDto);
}
