package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberContoller {

    private final MemberService memberService;

    /**
     * 회원 가입
     * http://localhost:8080/member/register
     */
    @PostMapping("/register")
    public MemberRegister.Response registerMember(
            @RequestBody MemberRegister.Request request
    ) {
        return memberService.registerMember(request);
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
