package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MemberService;
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

    @Override
    public MemberDto getMember() {
        return null;
    }

    @Override
    public MemberDto updateMember() {
        return null;
    }

    @Override
    public MemberDto deleteMember() {
        return null;
    }
}
