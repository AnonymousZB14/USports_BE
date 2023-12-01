package com.anonymous.usports.domain.member.service;


import com.anonymous.usports.domain.member.dto.*;

public interface MemberService {


    /**
     * 회원 가입
     */
    MemberRegister.Response registerMember(MemberRegister.Request request);

    /**
     * 회원 로그인
     */
    MemberDto loginMember(MemberLogin.Request request);

    /**
     * 회원 로그아웃
     */
    String logoutMember(String accessToken, String email);

    /**
     * 회원 삭제
     */
    MemberWithdraw.Response deleteMember(MemberDto memberDto, MemberWithdraw.Request request, Long memberId);


    /**
     * 회원 수정
     */
    MemberUpdate.Response updateMember(MemberUpdate.Request request, MemberDto memberDto, Long memberId);

    /**
     * 회원 비밀번호 수정
     */
    PasswordUpdate.Response updatePassword(PasswordUpdate.Request request, Long id, MemberDto memberDto);

    /**
     * 비밀번호 잃어버림
     */
    PasswordLostResponse.Response lostPassword(PasswordLostResponse.Request request);

    /**
     * 회원 이메일 인증 번호 재전송
     */
    MailResponse resendEmailAuth(MemberDto memberDto, Long memberId);

}
