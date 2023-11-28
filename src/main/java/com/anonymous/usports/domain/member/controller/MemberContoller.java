package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.*;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> registerMember(
            @RequestBody MemberRegister.Request request
    ) {
        return ResponseEntity.ok(memberService.registerMember(request));
    }

    /**
     * 로그인
     * http://localhost:8080/member/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody MemberLogin.Request request
    ){

        MemberDto memberDto = memberService.loginMember(request);

        TokenDto tokenDto = TokenDto.builder()
                .tokenType("Bearer ")
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
    public MemberWithdraw.Response deleteMember(
            @PathVariable("memberId") Long id,
            @RequestBody MemberWithdraw.Request request
            ){
        return memberService.deleteMember(request, id);
    }

    /**
     * 회원 수정
     * http://localhost:8080/member/{memberId}
     */
    @PutMapping("/{memberId}")
    public MemberUpdate.Response updateMember(
            @PathVariable("memberId") Long id,
            @RequestBody MemberUpdate.Request request
    ){
        return null;
    }

}
