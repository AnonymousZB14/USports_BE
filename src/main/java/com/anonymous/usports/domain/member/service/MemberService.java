package com.anonymous.usports.domain.member.service;


import com.anonymous.usports.domain.member.dto.MailResponse;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.dto.PasswordLostResponse;
import com.anonymous.usports.domain.member.dto.PasswordUpdate;
import com.anonymous.usports.domain.member.dto.frontResponse.MemberResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {


    /**
     * 회원 가입
     */
    MemberRegister.Response registerMember(MemberRegister.Request request);

    /**
     * 회원 로그인
     */
    MemberResponse loginMember(MemberLogin.Request request);

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
    MemberResponse updateMember(MemberUpdate.Request request, MemberDto memberDto, Long memberId);

    /**
     * 프로필 이미지 변경 / 삭제
     */
    MemberResponse updateMemberProfileImage(MultipartFile profileImage, MemberDto memberDto, Long memberId);

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
