package com.anonymous.usports.domain.profile.service;


import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.profile.dto.MemberProfile;
import com.anonymous.usports.domain.record.dto.RecordListDto;
import com.anonymous.usports.domain.recruit.dto.RecruitListDto;

public interface ProfileService {

  MemberProfile profileMember(String accountName, Long loginMemberId);

  RecordListDto profileRecords(String accountName, Integer page);

  RecruitListDto profileRecruits(String accountName, Integer page);
}
