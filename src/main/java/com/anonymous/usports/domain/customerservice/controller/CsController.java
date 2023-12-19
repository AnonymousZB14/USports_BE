package com.anonymous.usports.domain.customerservice.controller;

import com.anonymous.usports.domain.customerservice.dto.ChangeStatusDto;
import com.anonymous.usports.domain.customerservice.dto.CsDto;
import com.anonymous.usports.domain.customerservice.dto.CsListDto;
import com.anonymous.usports.domain.customerservice.dto.DeleteCS;
import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.customerservice.dto.UpdateCS;
import com.anonymous.usports.domain.customerservice.service.CsService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @DeleteMapping("/delete/{cs_id}")
  @ApiOperation(value = "신고 삭제하기", notes = "해당 글을 쓴 글쓴이가 자신의 신고글을 지울 수 있다. Admin은 모든 신고글을 지울 수 있다")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public DeleteCS.Response deleteCS(
    @PathVariable("cs_id") Long csId,
    @AuthenticationPrincipal MemberDto memberDto
  ) {
    return csService.deleteCs(csId, memberDto);
  }

  @PutMapping("/update/{cs_id}")
  @ApiOperation(value = "신고 수정하기", notes = "해당 글을 쓴 글쓴이가 자신의 신고글을 지울 수 있다.")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public UpdateCS.Response updateCs(
      @PathVariable("cs_id") Long csId,
      @RequestBody @Valid UpdateCS.Request request,
      @AuthenticationPrincipal MemberDto memberDto
  ) {
    return csService.updateCs(request,csId,memberDto);
  }

  @GetMapping("/{cs_id}")
  @ApiOperation(value = "신고글 보기", notes = "신고글의 자세한 내용을 볼 수 있다")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public CsDto getCsDetail(
      @PathVariable("cs_id") Long csId
  ) {
    return csService.getDetailCs(csId);
  }

  @GetMapping("")
  @ApiOperation(value = "나의 신고글 리스트 보기", notes = "수정한 날짜 기준으로 신고글을 리스트로 가지고 온다")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
  public CsListDto getCsList(
      @AuthenticationPrincipal MemberDto memberDto,
      @RequestParam(required = false, defaultValue = "1") int page
  ) {
    return csService.getCsList(memberDto, page);
  }

  /**
   * ========== admin 영역 ===========
   */

  @GetMapping("/admin")
  @ApiOperation(value = "신고글 찾기", notes = "Admin이 신고글 찾는 것")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public CsListDto getCsListAdmin(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) int statusNum,
      @AuthenticationPrincipal MemberDto memberDto
  ) {
    return csService.getCsListAdmin(memberDto, email, statusNum, page);
  }

  @PutMapping("/admin/{cs_id}")
  @ApiOperation(value = "신고글 상태 바꾸기", notes = "신고글에 대해 진행 상태를 어드민이 바꾸는 것")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ChangeStatusDto.Response changeCsStatus(
      @PathVariable("cs_id") Long csId,
      @RequestBody ChangeStatusDto.Request request,
      @AuthenticationPrincipal MemberDto memberDto
  ) {
    return csService.changeCsStatus(request, csId, memberDto);
  }
}
