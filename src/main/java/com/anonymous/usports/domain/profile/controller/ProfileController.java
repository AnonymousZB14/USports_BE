package com.anonymous.usports.domain.profile.controller;

import com.anonymous.usports.domain.profile.dto.ProfileRecords;
import com.anonymous.usports.domain.profile.dto.ProfileRecruits;
import com.anonymous.usports.domain.profile.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

  @ApiOperation("프로필 - 기록 글")
  @GetMapping("/{accountName}/records")
  public ResponseEntity<ProfileRecords> profileRecords(@PathVariable String accountName,
      @RequestParam(value = "page", defaultValue = "1") Integer page) {

    ProfileRecords profileRecords = profileService.profileRecords(accountName, page);

    return ResponseEntity.ok(profileRecords);
  }


  @ApiOperation("프로필 - 모집 글")
  @GetMapping("/{accountName}/recruits")
  public ResponseEntity<ProfileRecruits> profileRecruits(@PathVariable String accountName,
      @RequestParam(value = "page", defaultValue = "1") Integer page) {

    ProfileRecruits profileRecruits = profileService.profileRecruits(accountName, page);

    return ResponseEntity.ok(profileRecruits);
  }

}
