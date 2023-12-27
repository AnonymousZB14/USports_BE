package com.anonymous.usports.domain.profile.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.profile.dto.MemberProfile;
import com.anonymous.usports.domain.profile.service.ProfileService;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "프로필(Profile)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ProfileController {

  private final ProfileService profileService;

  @ApiOperation("프로필 - 회원 정보")
  @GetMapping("/profile/{accountName}")
  public ResponseEntity<MemberProfile> profileRecruits(
      @PathVariable String accountName,
      @AuthenticationPrincipal MemberDto loginMember) {

    MemberProfile memberProfile = profileService.profileMember(accountName, loginMember);

    return ResponseEntity.ok(memberProfile);
  }

  @ApiOperation("프로필 - 기록 글")
  @GetMapping("/profile/{accountName}/records")
  public ResponseEntity<RecordListDto> profileRecords(@PathVariable String accountName,
      @RequestParam(value = "page", defaultValue = "1") Integer page) {

    RecordListDto recordList = profileService.profileRecords(accountName, page);

    return ResponseEntity.ok(recordList);
  }


  @ApiOperation("프로필 - 모집 글")
  @GetMapping("/profile/{accountName}/recruits")
  public ResponseEntity<RecruitListDto> profileRecruits(@PathVariable String accountName,
      @RequestParam(value = "page", defaultValue = "1") Integer page) {

    RecruitListDto recruitList = profileService.profileRecruits(accountName, page);

    return ResponseEntity.ok(recruitList);
  }

}
