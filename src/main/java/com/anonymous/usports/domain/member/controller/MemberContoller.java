package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 회원 찾기
     */


    /**
     * 회원 삭제
     */

    /**
     * 회원 수정
     */


}
