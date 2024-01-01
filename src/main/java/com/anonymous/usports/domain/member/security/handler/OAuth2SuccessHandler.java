package com.anonymous.usports.domain.member.security.handler;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.TokenDto;
import com.anonymous.usports.domain.member.security.TokenProvider;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberDto memberDto = (MemberDto) authentication.getPrincipal();

        TokenDto token = tokenProvider.saveTokenInRedis(memberDto.getEmail());

        response.addHeader("Authorization", token.getAccessToken());

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:8080/oauth2/login/success");

    }
}
