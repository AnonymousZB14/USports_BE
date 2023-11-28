package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.*;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.global.constant.TokenConstant;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberContoller {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 회원 가입
     * http://localhost:8080/member/register
     */
    @PostMapping("/register")
    @ApiOperation(value = "회원 가입하기", notes = "회원 Entity 생성")
    public ResponseEntity<?> registerMember(
            @RequestBody @Valid MemberRegister.Request request
    ) {
        return ResponseEntity.ok(memberService.registerMember(request));
    }

    /**
     * 로그인
     * http://localhost:8080/member/login
     */
    // todo : refresh token 생성하기
    @PostMapping("/login")
    @ApiOperation(value = "회원 로그인 하기", notes = "access token과 refresh token 생성")
    public ResponseEntity<?> login(
            @RequestBody MemberLogin.Request request
    ){

        MemberDto memberDto = memberService.loginMember(request);

        TokenDto tokenDto = TokenDto.builder()
                .tokenType(TokenConstant.BEARER)
                .accessToken(tokenProvider.generateToken(
                        memberDto.getEmail(),
                        String.valueOf(memberDto.getRole())
                ))
                .build();

        return ResponseEntity.ok(MemberLogin.Response.builder()
                        .tokenDto(tokenDto)
                .build());
    }


    /**
     * 회원 삭제
     * http://localhost:8080/member/{memberId}/withdraw
     */
    @PostMapping("/{memberId}/withdraw")
    @ApiOperation(value = "회원 탈퇴하기", notes = "회원 삭제하기. ADMIN은 아무나 삭제가 가능해서 URI에 pathVariable로 memberId 넣기")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public MemberWithdraw.Response deleteMember(
            @PathVariable("memberId") Long id,
            @AuthenticationPrincipal MemberDto memberDto,
            @RequestBody MemberWithdraw.Request request
            ){
        return memberService.deleteMember(memberDto, request, id);
    }

    /**
     * 회원 수정
     * http://localhost:8080/member/{memberId}
     */
    @PutMapping("/{memberId}")
    @ApiOperation(value = "회원 정보 수정하기", notes = "ADMIN은 아무나 삭제가 가능해서 URI에 pathVariable로 memberId 넣기")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public MemberUpdate.Response updateMember(
            @PathVariable("memberId") Long id,
            @RequestBody @Valid MemberUpdate.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return memberService.updateMember(request, memberDto, id);
    }

}
