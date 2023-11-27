package com.anonymous.usports.domain.recruit.service;


import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;

public interface RecruitService {

  RecruitDto addRecruit(RecruitRegister.Request request, Long memberId);

}
