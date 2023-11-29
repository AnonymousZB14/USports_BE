package com.anonymous.usports.domain.follow.controller;

import com.anonymous.usports.domain.follow.dto.FollowDelete;
import com.anonymous.usports.domain.follow.dto.FollowDto;
import com.anonymous.usports.domain.follow.dto.FollowRegister;
import com.anonymous.usports.domain.follow.dto.FollowRegister.Response;
import com.anonymous.usports.domain.follow.dto.FollowResponse;
import com.anonymous.usports.domain.follow.service.FollowService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @PostMapping("/follow/{toMemberId}")
  public ResponseEntity<FollowResponse> changeFollow(
      @PathVariable Long toMemberID,
      @AuthenticationPrincipal MemberDto member) {

    FollowDto result = followService.changeFollow(member.getMemberId(), toMemberID);
    if(result!=null) {
      return ResponseEntity.ok(new Response(result));
    }
    return ResponseEntity.ok(new FollowDelete.Response());
  }

}
