package com.anonymous.usports.domain.customerservice.controller;

import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.customerservice.service.CsService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Customer Service")
@RestController
@RequestMapping("/cs")
@RequiredArgsConstructor
public class CsController {

  private final CsService csService;

  @PostMapping("/register")
  @ApiOperation(value = "신고 등록하기", notes = "유저가 신고를 등록할 수 있다")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public RegisterCS.Response registerCS(
      @RequestBody @Valid RegisterCS.Request request,
      @AuthenticationPrincipal MemberDto memberDto
  ){
    return csService.registerCs(request, memberDto);
  }
}
