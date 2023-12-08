package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.service.impl.CustomOAuth2MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final CustomOAuth2MemberService customOAuth2MemberService;

    private static String authorizationRequestBaseUri = "/oauth2/authorization";
    Map<String, String> oauth2AuthenicationUrls = new HashMap<>();

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * 테스트 용
     */
    @GetMapping("")
    public String login(Model model){

        log.info("hello");
        return "login";
    }
}
