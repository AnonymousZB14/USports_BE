package com.anonymous.usports.websocket.controller;

import com.anonymous.usports.websocket.dto.MarkAsReadRequestDto;
import com.anonymous.usports.websocket.service.ChatPartakeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "채팅 참여(ChatPartake)")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatPartakeController {

  private final ChatPartakeService chatPartakeService;

  // 웹 소켓 연결이 끊어질 때 해당 api를 호출해서 마지막 읽은 chatId를 등록한다.
  @ApiOperation("마지막 채팅 ID 체크 후 DB 저장")
  @PostMapping("/markchat")
  public ResponseEntity<MarkAsReadRequestDto.Response> markChatAsRead(
      @RequestBody MarkAsReadRequestDto.Request request
  ) {
    log.info("chatRoomId:"+request.getChatRoomId());
    log.info("userId:"+request.getUserId());
    MarkAsReadRequestDto.Response response =
        chatPartakeService.markChatAsRead(request.getChatRoomId(), request.getUserId());
    return ResponseEntity.ok(response);
  }

}
