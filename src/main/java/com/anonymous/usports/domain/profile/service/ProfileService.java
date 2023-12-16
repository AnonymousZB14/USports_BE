package com.anonymous.usports.domain.profile.service;


import com.anonymous.usports.domain.profile.dto.ProfileMember;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;

public interface ProfileService {

  ProfileMember profileMember(String accountName);

  RecordListDto profileRecords(String accountName, Integer page);

  RecruitListDto profileRecruits(String accountName, Integer page);
}
