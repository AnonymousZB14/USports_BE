package com.anonymous.usports.domain.customerservice.service.impl;

import com.anonymous.usports.domain.customerservice.dto.ChangeStatusDto;
import com.anonymous.usports.domain.customerservice.dto.CsDto;
import com.anonymous.usports.domain.customerservice.dto.CsListDto;
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
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        .csStatus(CsStatus.REGISTERED)
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
    } else {
      if (member.equals(cs.getMemberEntity())) {
        csRepository.delete(cs);
      } else {
        throw new CsException(ErrorCode.CS_NOT_WRITTEN_BY_CURRENT_USER);
      }
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

  @Override
  public CsDto getDetailCs(Long csId) {
    return CsDto.fromEntity(csRepository.findById(csId)
        .orElseThrow(() -> new CsException(ErrorCode.NO_CS_FOUND)));
  }

  @Override
  public CsListDto getCsList(MemberDto memberDto, int page) {

    MemberEntity member = memberRepository.findById(memberDto.getMemberId())
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Page<CsEntity> csListPage = csRepository.findAllByMemberEntityOrderByUpdatedAtDesc(
        member, PageRequest.of(page - 1, CsConstant.maxElements));

    if (page < 1 || page > csListPage.getTotalPages())
      throw new CsException(ErrorCode.PAGE_NOT_FOUND);

    List<CsDto> csList = csListPage.getContent().stream()
        .map(CsDto::fromEntity).collect(Collectors.toList());

    return CsListDto.builder()
        .allElementsCount(csListPage.getNumberOfElements())
        .elementsInPage(csList.size())
        .csList(csList)
        .pageNum(page)
        .totalPageNum(csListPage.getTotalPages())
        .build();
  }

  @Override
  public CsListDto getCsListAdmin(MemberDto member, String email, String statusNum, int page) {

    if (!Role.ADMIN.equals(member.getRole()))
      throw new MemberException(ErrorCode.NO_AUTHORITY_ERROR);

    MemberEntity memberEntity = null;

    if (email != null) {
      memberEntity = memberRepository.findByEmail(email)
          .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    CsStatus csStatus = null;

    if (statusNum != null) {
      if (statusNum.equals("1")) {
        csStatus = CsStatus.REGISTERED;
      } else if (statusNum.equals("2")) {
        csStatus = CsStatus.ING;
      } else if (statusNum.equals("3")) {
        csStatus = CsStatus.FINISHED;
      }
    }

    Page<CsEntity> csPage = csRepository.findAllByConditionsFromAdmin(
        memberEntity, csStatus, PageRequest.of(page - 1, CsConstant.maxElements));

    log.info("{}", csPage.getContent());

    if (page < 1 || page > csPage.getTotalPages())
      throw new CsException(ErrorCode.PAGE_NOT_FOUND);

    List<CsDto> csList = csPage.getContent().stream()
        .map(CsDto::fromEntity).collect(Collectors.toList());

    return CsListDto.builder()
        .totalPageNum(csPage.getTotalPages())
        .pageNum(page)
        .elementsInPage(csList.size())
        .allElementsCount(csPage.getNumberOfElements())
        .csList(csList)
        .build();
  }

  @Override
  @Transactional
  public ChangeStatusDto.Response changeCsStatus(ChangeStatusDto.Request request, Long csId, MemberDto memberDto) {

    // 어드민만 변경할 수 있는 내용
    if (!Role.ADMIN.equals(memberDto.getRole()))
      throw new MemberException(ErrorCode.NO_AUTHORITY_ERROR);

    CsEntity cs = csRepository.findById(csId)
        .orElseThrow(() -> new CsException(ErrorCode.NO_CS_FOUND));

    String csStatus;

    if (request.getStatusNum() == 1) {
      cs.updateStatus(CsStatus.REGISTERED);
      csStatus = CsStatus.REGISTERED.getDescription();

    } else if (request.getStatusNum() == 2) {
      cs.updateStatus(CsStatus.ING);
      csStatus = CsStatus.ING.getDescription();

    } else if (request.getStatusNum() == 3) {
      cs.updateStatus(CsStatus.FINISHED);
      csStatus = CsStatus.FINISHED.getDescription();

    } else {
      throw new CsException(ErrorCode.INPUT_VALUE_NOT_EXIST_FOR_CS_STATUS);
    }

    return new ChangeStatusDto.Response(CsConstant.CS_STATUS_SUCCESSFULLY_CHANGED,
        csStatus);
  }
}
