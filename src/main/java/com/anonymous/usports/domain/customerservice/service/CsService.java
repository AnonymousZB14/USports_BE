package com.anonymous.usports.domain.customerservice.service;

import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.member.dto.MemberDto;

public interface CsService {

  RegisterCS.Response registerCs(RegisterCS.Request request, MemberDto memberDto);

}
