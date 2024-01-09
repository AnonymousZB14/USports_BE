package com.anonymous.usports.domain.member.security.handler;

import com.anonymous.usports.global.constant.UrlConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
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
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (authentication.isAuthenticated()) {
      getRedirectStrategy().sendRedirect(request, response,
          UrlConstant.USPORTS_URL + "/oauth2/login/success");
    } else {
      throw new MemberException(ErrorCode.NO_AUTHORITY_ERROR);
    }
  }
}