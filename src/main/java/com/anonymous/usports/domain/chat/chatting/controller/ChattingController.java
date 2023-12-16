package com.anonymous.usports.domain.chat.chatting.controller;

import com.anonymous.usports.domain.chat.chatting.dto.ChattingDto;
import com.anonymous.usports.domain.chat.chatting.dto.ChattingInput;
import com.anonymous.usports.domain.chat.chatting.service.ChattingService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "채팅 메시지 (Chatting)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ChattingController {

  private final ChattingService chattingService;


  @PostMapping("/chat/test")
  public ResponseEntity<?> methodName(@RequestBody ChattingInput input,
      @AuthenticationPrincipal
      MemberDto loginMember){
    ChattingDto save = chattingService.save(loginMember.getMemberId(), input);
    log.info("save dto : {}", save);
    return ResponseEntity.ok(save);
  }

  @GetMapping("/chatroom/test/{id}")
  public ResponseEntity<?> methodName(@PathVariable("id") Long chatRoomId){
    List<ChattingDto> list = chattingService.getAllChattingByChatRoom(chatRoomId);
    return ResponseEntity.ok(list);
  }
}
