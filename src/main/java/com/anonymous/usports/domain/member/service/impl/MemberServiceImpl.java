package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberUpdate;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.global.type.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private void checkRepitition(String accountName, String email, String phoneNumber){
        if (memberRepository.existsByAccountName(accountName)) {
            throw new RuntimeException("닉네임이 존재합니다");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("이메일이 존재합니다");
        }

        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("핸드폰 번호가 이미 존재합니다");
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

        checkRepitition(request.getAccountName(), request.getEmail(), request.getPhoneNumber());

        return saveMember(request);
    }

    public MemberEntity passwordCheck(Long memberId, String password) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("계정이 존재하지 않습니다"));

        if (!memberEntity.getPassword().equals(password))
            throw new RuntimeException("비밀번호가 일치하지 않습니다");

        return memberEntity;
    }

    @Override
    public MemberWithdraw.Response deleteMember(MemberWithdraw.Request request, Long memberId) {

        memberRepository.delete(passwordCheck( memberId, request.getPassword()));

        return MemberWithdraw.Response.builder()
                .message(Message.DELETE_SUCCESS.getDescription())
                .build();
    }

    @Override
    public MemberUpdate.Response updateMember(MemberUpdate.Request request, Long memberId) {
        return null;
    }


}
