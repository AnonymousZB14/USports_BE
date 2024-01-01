package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.frontResponse.MemberResponse;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "OAuth2")
@RestController
@RequestMapping("/oauth2/login")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final MemberServiceImpl memberService;

    @GetMapping("/success")
    public MemberResponse oauthLogin(
        @AuthenticationPrincipal MemberDto memberDto
    ) {
        return memberService.oauthLogin(memberDto.getMemberId());
    }

}
