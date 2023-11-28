package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.*;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private void checkDuplication(String accountName, String email, String phoneNumber){
        if (memberRepository.existsByAccountName(accountName)) {
            throw new MemberException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new MemberException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private MemberRegister.Response saveMember(MemberRegister.Request request) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        log.info(request.getPassword());

        memberRepository.save(MemberRegister.Request.toEntity(request));

        return MemberRegister.Response.fromEntity(
                MemberRegister.Request.toEntity(request)
        );
    }

    @Override
    public MemberRegister.Response registerMember(MemberRegister.Request request) {

        checkDuplication(request.getAccountName(), request.getEmail(), request.getPhoneNumber());

        return saveMember(request);
    }

    @Override
    public MemberDto loginMember(MemberLogin.Request request) {

        MemberDto memberDto = (MemberDto) loadUserByUsername(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), memberDto.getPassword())) {
            throw new MemberException(ErrorCode.PASSWORD_UNMATCH);
        }

        return memberDto;
    }

    public MemberEntity passwordCheck(Long memberId, String password) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberEntity.getPassword().equals(password))
            throw new MemberException(ErrorCode.PASSWORD_UNMATCH);

        return memberEntity;
    }

    @Override
    public MemberWithdraw.Response deleteMember(MemberWithdraw.Request request, Long memberId) {

        memberRepository.delete(passwordCheck( memberId, request.getPassword()));

        return MemberWithdraw.Response.builder()
                .message(ResponseConstant.MEMBER_DELETE_SUCCESS)
                .build();
    }

    @Override
    public MemberUpdate.Response updateMember(MemberUpdate.Request request, Long memberId) {
        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return MemberDto.fromEntity(memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)));
    }
}
