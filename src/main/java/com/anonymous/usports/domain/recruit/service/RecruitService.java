package com.anonymous.usports.domain.recruit.service;


import com.anonymous.usports.domain.recruit.dto.AddRecruit;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;

public interface RecruitService {

  RecruitDto addRecruit(AddRecruit.Request request, Long memberId);

}
