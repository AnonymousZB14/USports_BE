package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;
import com.anonymous.usports.websocket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/{chatRoomId}")
    public String enterChatRoom(
        @PathVariable Long chatRoomId,
        @AuthenticationPrincipal MemberDto memberDto
    ) {
        return chatRoomService.enterChatRoom(chatRoomId, memberDto);
    }

    @GetMapping("/list")
    public List<ChatPartakeDto>  getChatRoomList(
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return chatRoomService.getChatRoomList(memberDto);
    }

    @PostMapping("/direct-message")
    public CreateDMDto.Response createDM(
            @RequestBody  CreateDMDto.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return chatRoomService.createChatRoom(request, memberDto);
    }

    @PostMapping("/recruit")
    public CreateRecruitChat.Response createRecruitChat (
        @RequestBody CreateRecruitChat.Request request,
        @AuthenticationPrincipal MemberDto memberDto
        ) {
        return chatRoomService.createRecruitChat(request, memberDto);
    }

    @PostMapping("/invite")
    public ChatInviteDto.Response inviteToRecruitChat(
            @RequestBody ChatInviteDto.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return chatRoomService.inviteMemberToRecruitChat(request, memberDto);
    }

    @DeleteMapping("/{chatRoomId}/exit")
    public String exitChat(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return chatRoomService.exitChat(chatRoomId, memberDto);
    }
}
