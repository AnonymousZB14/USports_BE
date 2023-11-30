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



}
