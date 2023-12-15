package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.*;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.domain.notification.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
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
    private final NotificationService notificationService;

    /**
     * 회원 가입
     * http://localhost:8080/member/register
     */
    @PostMapping("/register")
    @ApiOperation(value = "회원 가입하기", notes = "회원 Entity 생성")
    public ResponseEntity<MemberRegister.Response> registerMember(
            @RequestBody @Valid MemberRegister.Request request
    ) {
        return ResponseEntity.ok(memberService.registerMember(request));
    }


    /**
     * 로그인
     * http://localhost:8080/member/login
     */
    @PostMapping("/login")
    @ApiOperation(value = "회원 로그인 하기", notes = "access token과 refresh token 생성")
    public ResponseEntity<MemberLogin.Response> login(
            @RequestBody MemberLogin.Request request,
        HttpServletRequest httpServletRequest
    ){

        MemberDto memberDto = memberService.loginMember(request);

        notificationService.checkUnreadNotificationAndSetSession(memberDto.getMemberId(), httpServletRequest);

        return ResponseEntity.ok(MemberLogin.Response.builder()
                .member(memberDto)
                        .tokenDto(tokenProvider.saveTokenInRedis(memberDto.getEmail()))
                .build());
    }

    /**
     * 토큰 재발급
     * http://localhost:8080/member/login/reissue
     * access token이 만료될 때에, refresh token을 확인하고, access token과 refresh token을 재발급해준다
     */
    @PostMapping("/login/reissue")
    @ApiOperation(value = "Access token 재발급하기", notes = "refresh token 확인 후, Access token 재발급하기")
    public ResponseEntity<TokenDto> reissueAccessToken(
        @RequestHeader("RefreshToken") String refreshToken
    ){
        return ResponseEntity.ok(tokenProvider.regenerateToken(refreshToken));
    }

    /**
     * 로그아웃
     * http://localhost:8080/member/logout
     */
    @PostMapping("/logout")
    @ApiOperation(value="로그아웃", notes="refreshToken을 삭제하고, access token을 blackList로 돌린다")
    @PreAuthorize("hasAnyRole('ROLE_UNAUTH', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<String> memberLogout(
            @AuthenticationPrincipal MemberDto memberDto,
            @RequestHeader("Authorization") String accessToken
    ) {
        String token = tokenProvider.resolveTokenFromRequest(accessToken);

        return ResponseEntity.ok(memberService.logoutMember(token, memberDto.getEmail()));
    }

    /**
     * 회원 삭제
     * http://localhost:8080/member/{memberId}/withdraw
     */
    @PostMapping("/{memberId}/withdraw")
    @ApiOperation(value = "회원 탈퇴하기", notes = "회원 삭제하기. ADMIN은 아무나 삭제가 가능해서 URI에 pathVariable로 memberId 넣기")
    @PreAuthorize("hasAnyRole('ROLE_UNAUTH', 'ROLE_ADMIN', 'ROLE_USER')")
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
    @PreAuthorize("hasAnyRole('ROLE_UNAUTH', 'ROLE_ADMIN', 'ROLE_USER')")
    public MemberUpdate.Response updateMember(
            @PathVariable("memberId") Long id,
            @RequestBody @Valid MemberUpdate.Request request,
            @AuthenticationPrincipal MemberDto memberDto
    ){
        return memberService.updateMember(request, memberDto, id);
    }

    /**
     * 회원 비밀번호 수정
     * http://localhost:8080/member/{memberId}/edit-password
     */
    @PutMapping("/{memberId}/edit-password")
    @PreAuthorize("hasAnyRole('ROLE_UNAUTH', 'ROLE_USER')")
    @ApiOperation(value="회원 비밀번호 수정", notes="회원 기존 비밀번호를 입력하고, 새로운 비밀번호와 그 비밀번호와 일치하는지 확인을 한다")
    public PasswordUpdate.Response changePassword(
            @RequestBody @Valid PasswordUpdate.Request request,
            @PathVariable("memberId") Long id,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return memberService.updatePassword(request, id, memberDto);
    }

    /**
     * 회원이 비밀번호를 잃어버렸을 경우
     * http://localhost:8080/member/password-lost
     */
    @PostMapping("/password-lost")
    @ApiOperation(value="비밀번호를 잃어버렸을 경우", notes="이메일과 핸드폰번호를 확인한 후, 이메일로 임시비밀번호를 보내준다")
    public PasswordLostResponse.Response passwordLost(
            @RequestBody PasswordLostResponse.Request request
    ) {
        return memberService.lostPassword(request);
    }

    /**
     * 회원 인증 번호 재전송
     * http://localhost:8080/member/{memberId}/resend-email-auth
     */
    @PreAuthorize("hasAnyRole('ROLE_UNAUTH')")
    @GetMapping("/{memberId}/resend-email-auth")
    @ApiOperation(value="회원 인증 번호 재전송", notes="이메일 인증이 만료 되었을 때 회원 인증 번호 재전송")
    public MailResponse resendEmailAuth(
            @PathVariable("memberId") Long id,
            @AuthenticationPrincipal MemberDto memberDto
    ) {
        return memberService.resendEmailAuth(memberDto, id);
    }

}
