package com.anonymous.usports.domain.profile.service;


import com.anonymous.usports.domain.profile.dto.ProfileRecords;
import com.anonymous.usports.domain.profile.dto.ProfileRecruits;

public interface ProfileService {

  ProfileRecords profileRecords(String accountName, Integer page);

  ProfileRecruits profileRecruits(String accountName, Integer page);
}
