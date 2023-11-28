package com.anonymous.usports.domain.member.service;


import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;

public interface MemberService {


    /**
     * 회원 가입
     */
    MemberRegister.Response registerMember(MemberRegister.Request request);

    /**
     * 회원 삭제
     */
    MemberWithdraw.Response deleteMember(MemberWithdraw.Request request, Long memberId);


    /**
     * 회원 수정
     */
    MemberUpdate.Response updateMember(MemberUpdate.Request request ,Long memberId);



}
