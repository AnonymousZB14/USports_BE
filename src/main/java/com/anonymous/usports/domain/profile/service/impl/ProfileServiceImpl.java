package com.anonymous.usports.domain.profile.service.impl;

import com.anonymous.usports.domain.mypage.service.MyPageService;
import com.anonymous.usports.domain.profile.dto.ProfileRecords;
import com.anonymous.usports.domain.profile.dto.ProfileRecruits;
import com.anonymous.usports.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

  private final MyPageService myPageService;

  @Override
  public ProfileRecords profileRecords(String accountName, Integer page) {
    
    return null;
  }

  @Override
  public ProfileRecruits profileRecruits(String accountName, Integer page) {

    return null;
  }
}
