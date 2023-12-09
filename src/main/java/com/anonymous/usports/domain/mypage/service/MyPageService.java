package com.anonymous.usports.domain.mypage.service;

import com.anonymous.usports.domain.mypage.dto.MyPageMainDto;

public interface MyPageService {

  MyPageMainDto getMyPageMainData(Long memberId);
}
