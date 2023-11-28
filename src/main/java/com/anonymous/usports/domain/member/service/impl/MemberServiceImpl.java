package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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


}
