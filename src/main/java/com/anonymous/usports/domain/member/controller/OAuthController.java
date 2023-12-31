package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.service.impl.CustomOAuth2MemberService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "OAuth2")
@RestController
@RequestMapping("/oauth2/login")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final CustomOAuth2MemberService customOAuth2MemberService;

    /**
     * 테스트 용
     */
    @GetMapping("/success")
    public MemberLogin.Response loginSuccess(){
        return MemberLogin.Response.builder()
            .tokenDto()
            .memberResponse()
            .build();
    }
//    @GetMapping("/code/{registrationId}")
//    public String login(
//        @RequestParam String code,
//        @RequestParam String state,
//        @PathVariable String registrationId
//    ){
//
//        return code;
//    }
}
