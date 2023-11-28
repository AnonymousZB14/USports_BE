package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.*;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final InterestedSportsRepository interestedSportsRepository;
    private final SportsRepository sportsRepository;
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

    public MemberEntity passwordCheck(MemberDto memberDto, Long memberId, String password) {

        if (memberDto.getRole() != Role.ADMIN && memberDto.getMemberId() != memberId) {
            throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);
        }

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberEntity.getPassword().equals(password))
            throw new MemberException(ErrorCode.PASSWORD_UNMATCH);

        return memberEntity;
    }

    @Override
    public MemberWithdraw.Response deleteMember(MemberDto memberDto, MemberWithdraw.Request request, Long memberId) {

        memberRepository.delete(passwordCheck(memberDto, memberId, request.getPassword()));

        return MemberWithdraw.Response.builder()
                .message(ResponseConstant.MEMBER_DELETE_SUCCESS)
                .build();
    }


    private MemberEntity checkDuplicationUpdate(MemberDto memberDto, MemberUpdate.Request request){

        MemberEntity memberEntity = memberRepository.findById(memberDto.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (!memberEntity.getAccountName().equals(request.getAccountName())) {
            if (memberRepository.existsByAccountName(request.getAccountName())) {
                throw new MemberException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
            }
        }

        if (!memberEntity.getEmail().equals(request.getEmail())) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new MemberException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        if (!memberEntity.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new MemberException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        return memberEntity;
    }

    // 관심 운동이 있을 경우, 수정을 했을 상황을 대비해, 이미 저장되어 있는 데이터는 다 삭제하고 다시 저장하기
    // 관심 운동 있으면 회원 상태를 ACTIVE으로 전환
    private List<InterestedSportsEntity> saveInterestedSports(List<Long> allSelectedSports, MemberEntity memberEntity){

        List<InterestedSportsEntity> list = new ArrayList<>();

        list.addAll(allSelectedSports.stream()
                .map(id -> InterestedSportsEntity.builder()
                        .sports(sportsRepository.findById(id)
                                .orElseThrow(() -> new MyException(ErrorCode.SPORTS_NOT_FOUND)))
                        .memberEntity(memberEntity)
                        .build())
                .collect(Collectors.toList()));

        interestedSportsRepository.deleteAllByMemberEntity(memberEntity);

        return list;
    }

    @Override
    public MemberUpdate.Response updateMember(MemberUpdate.Request request, MemberDto memberDto, Long memberId) {

        if (memberDto.getRole() != Role.ADMIN && memberDto.getMemberId() != memberId) {
            throw new MemberException(ErrorCode.MEMBER_ID_UNMATCH);
        }

        // 닉네임, 이메일, 핸드폰 번호를 수정 할 때, 겹치지 않게
        // 하는 김에 MemberEntity 가지고 오기
        MemberEntity memberEntity = checkDuplicationUpdate(memberDto, request);

        // 관심 운동이 아예 없으면 안 된다
        if (request.getInterestedSports().size() == 0) {
            throw new MemberException(ErrorCode.NEED_AT_LEAST_ONE_SPORTS);
        }

        // 맴버 entity 수정
        memberEntity.updateMember(request);

        // 관심 운동 저장
        interestedSportsRepository.saveAll(saveInterestedSports(
                request.getInterestedSports(),
                memberEntity
        ));

        List<String> sportsList = interestedSportsRepository.findAllByMemberEntity(memberEntity)
                .stream()
                .map(s -> s.getSports().getSportsName())
                .collect(Collectors.toList());

        MemberUpdate.Response response = MemberUpdate.Response.fromEntity(memberEntity);
        response.setInterestedSports(sportsList);

        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return MemberDto.fromEntity(memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)));
    }
}
