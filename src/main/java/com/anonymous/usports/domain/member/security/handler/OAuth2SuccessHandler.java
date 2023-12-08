package com.anonymous.usports.domain.member.security.handler;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.TokenDto;
import com.anonymous.usports.domain.member.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberDto memberDto = (MemberDto) authentication.getPrincipal();

        log.info("{}", memberDto.getAttributes());
        log.info("OAuth2 인증 성공");

        TokenDto token = tokenProvider.saveTokenInRedis(memberDto.getEmail());

        log.info("{}", token.getAccessToken());
        log.info("{}", token.getRefreshToken());
    }
}
