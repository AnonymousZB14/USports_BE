package com.anonymous.usports.domain.customerservice.service.impl;

import com.anonymous.usports.domain.customerservice.dto.DeleteCS;
import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.customerservice.dto.UpdateCS;
import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.customerservice.repository.CsRepository;
import com.anonymous.usports.domain.customerservice.service.CsService;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.constant.CsConstant;
import com.anonymous.usports.global.exception.CsException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.CsStatus;
import com.anonymous.usports.global.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CsServiceImpl implements CsService {

  private final CsRepository csRepository;
  private final MemberRepository memberRepository;

  @Override
  public RegisterCS.Response registerCs(RegisterCS.Request request, MemberDto memberDto) {

    MemberEntity member = memberRepository.findById(memberDto.getMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    CsEntity cs = CsEntity.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .memberEntity(member)
        .csStatus(CsStatus.Registered)
        .build();

    csRepository.save(cs);

    return RegisterCS.Response.fromEntity(cs);
  }

  private DeleteCS.Response deleteCSById(Long csId, MemberDto memberDto) {
    MemberEntity member = memberRepository.findById(memberDto.getMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    CsEntity cs = csRepository.findById(csId)
        .orElseThrow(() -> new CsException(ErrorCode.NO_CS_FOUND));

    if (Role.ADMIN.equals(member.getRole())) {
      csRepository.delete(cs);
    }

    if (member.equals(cs.getMemberEntity())) {
      csRepository.delete(cs);
    }

    return new DeleteCS.Response(csId, CsConstant.SUCCESSFULLY_DELETED);
  }

  @Override
  public DeleteCS.Response deleteCs(Long csId, MemberDto memberDto) {
    return deleteCSById(csId, memberDto);
  }

  @Override
  @Transactional
  public UpdateCS.Response updateCs(UpdateCS.Request request, Long csId, MemberDto memberDto) {

    CsEntity cs = csRepository.findById(csId)
        .orElseThrow(() -> new CsException(ErrorCode.NO_CS_FOUND));

    if (!memberDto.getMemberId().equals(cs.getMemberEntity().getMemberId()))
      throw new CsException(ErrorCode.CS_NOT_WRITTEN_BY_CURRENT_USER);

    CsStatus curStatus = cs.getCsStatus();

    cs.updateCs(request, curStatus);

    return new UpdateCS.Response(request.getTitle(), request.getContent(), CsConstant.SUCCESSFULLY_UPDATED);
  }
}
