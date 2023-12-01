package com.anonymous.usports.domain.recruit.service;


import com.anonymous.usports.domain.recruit.dto.RecruitEndResponse;
import com.anonymous.usports.domain.recruit.dto.RecruitRegister;
import com.anonymous.usports.domain.recruit.dto.RecruitDto;
import com.anonymous.usports.domain.recruit.dto.RecruitUpdate;

public interface RecruitService {

  /**
   * Recruit 등록
   */
  RecruitDto registerRecruit(RecruitRegister.Request request, Long loginMemberId);

  /**
   * Recruit 조회
   */
  RecruitDto getRecruit(Long recruitId);

  /**
   * Recruit 수정
   */
  RecruitDto updateRecruit(RecruitUpdate.Request request, Long recruitId, Long loginMemberId);

  /**
   * Recruit 삭제
   */
  RecruitDto deleteRecruit(Long recruitId, Long loginMemberId);

  /**
   * 모집 마감
   */
  RecruitEndResponse endRecruit(Long recruitId, Long loginMemberId);

}
