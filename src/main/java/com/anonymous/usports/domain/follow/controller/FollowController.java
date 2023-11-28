package com.anonymous.usports.domain.follow.controller;

import com.anonymous.usports.domain.follow.dto.FollowDelete;
import com.anonymous.usports.domain.follow.dto.FollowDto;
import com.anonymous.usports.domain.follow.dto.FollowRegister;
import com.anonymous.usports.domain.follow.dto.FollowRegister.Response;
import com.anonymous.usports.domain.follow.service.FollowService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {
  private final FollowService followService;

  @PostMapping("/follow/{toMemberId}")
  public ResponseEntity<FollowRegister.Response> registerFollow(
      @PathVariable Long toMemberID,
      @AuthenticationPrincipal MemberDto member) {

    FollowDto result = followService.followMember(member.getMemberId(), toMemberID);
    return ResponseEntity.ok(new Response(result));
  }

  @DeleteMapping("/follow/{followId}")
  public ResponseEntity<FollowDelete.Response> deleteFollow(
      @PathVariable Long followId,
      @AuthenticationPrincipal MemberDto member) {

    FollowDto result = followService.deleteFollow(followId, member.getMemberId());
    return ResponseEntity.ok(new FollowDelete.Response(result));
  }

}
