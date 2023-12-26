package com.anonymous.usports.websocket.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatEnterDto;
import com.anonymous.usports.websocket.dto.ChatMessageDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;

import java.util.List;

public interface ChatRoomService {

    ChatEnterDto enterChatRoom(Long chatRoomId, MemberDto memberDto);

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
    List<ChatMessageDto> getMessageList(Long chatRoomId, MemberDto memberDto);
}
