package com.anonymous.usports.domain.member.service;


import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberRegister;

public interface MemberService {


    /**
     * 회원 가입
     */
    MemberRegister.Response registerMember(MemberRegister.Request request);

    /**
     * 회원 찾기
     */
    MemberDto getMember();


    /**
     * 회원 수정
     */
    MemberDto updateMember();

        /**
         * 회원 삭제
         */
    MemberDto deleteMember();

}
