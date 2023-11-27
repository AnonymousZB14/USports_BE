package com.anonymous.usports.domain.member.service;


import com.anonymous.usports.domain.member.dto.MemberDto;

public interface MemberService {

    MemberDto registerMember();

    MemberDto getMember();

    MemberDto updateMember();

    MemberDto deleteMember();

}
