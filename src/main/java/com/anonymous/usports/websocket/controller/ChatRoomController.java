package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.websocket.dto.ChatEnterDto;
import com.anonymous.usports.websocket.dto.ChatPartakeDto;
import com.anonymous.usports.websocket.dto.httpbody.ChatInviteDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateDMDto;
import com.anonymous.usports.websocket.dto.httpbody.CreateRecruitChat;
import com.anonymous.usports.websocket.service.ChatRoomService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "채팅방(ChatRoom)")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatEnterDto> enterChatRoom(
        @PathVariable Long chatRoomId,
        @AuthenticationPrincipal MemberDto memberDto
    ) {
        return ResponseEntity.ok(
            chatRoomService.enterChatRoom(chatRoomId, memberDto));
    }


    @GetMapping("/list")
    public ResponseEntity<List<ChatPartakeDto>>  getChatRoomList(
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return ResponseEntity.ok(
            chatRoomService.getChatRoomList(memberDto));
    }

    @PostMapping("/direct-message")
    public ResponseEntity<CreateDMDto.Response> createDM(
            @RequestBody  CreateDMDto.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return ResponseEntity.ok(
            chatRoomService.createChatRoom(request, memberDto));
    }

    @PostMapping("/recruit")
    public ResponseEntity<CreateRecruitChat.Response> createRecruitChat (
        @RequestBody CreateRecruitChat.Request request,
        @AuthenticationPrincipal MemberDto memberDto
        ) {
        return ResponseEntity.ok(
            chatRoomService.createRecruitChat(request, memberDto));
    }

    @PostMapping("/invite")
    public ResponseEntity<ChatInviteDto.Response> inviteToRecruitChat(
            @RequestBody ChatInviteDto.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return ResponseEntity.ok(
            chatRoomService.inviteMemberToRecruitChat(request, memberDto));
    }

    @DeleteMapping("/{chatRoomId}/exit")
    public ResponseEntity<String> exitChat(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return ResponseEntity.ok(
            chatRoomService.exitChat(chatRoomId, memberDto));
    }
}
