package com.anonymous.usports.domain.recruit.service;


import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate.Request;

public interface RecruitService {

  RecruitDto addRecruit(RecruitRegister.Request request, Long memberId);

  RecruitDto getRecruit(Long recruitId);

  RecruitDto updateRecruit(RecruitUpdate.Request request, Long recruitId, Long memberId);

  RecruitDto deleteRecruit(Long recruitId, Long memberId);

}
