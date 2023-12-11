package com.anonymous.usports.domain.mongodbtest;

import com.anonymous.usports.domain.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/chatroom/{id}")
  public ResponseEntity<?> methodName(@PathVariable("id") Long chatRoomId){
    List<ChattingDto> list = chattingService.getAllChattingByChatRoom(chatRoomId);
    return ResponseEntity.ok(list);
  }
}
