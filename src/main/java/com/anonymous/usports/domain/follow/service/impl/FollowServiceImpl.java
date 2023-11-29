package com.anonymous.usports.domain.follow.service.impl;

import com.anonymous.usports.domain.follow.dto.FollowDto;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.follow.service.FollowService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.FollowException;
import com.anonymous.usports.global.exception.MyException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;


  @Override
  public FollowDto changeFollow(Long fromMemberId, Long toMemberId) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(()->new MyException(ErrorCode.MEMBER_NOT_FOUND));

    MemberEntity toMember = memberRepository.findById(toMemberId)
        .orElseThrow(()->new MyException(ErrorCode.MEMBER_NOT_FOUND));

    Optional<FollowEntity> existingFollow = followRepository.findByFromMemberAndToMember(fromMember, toMember);

    FollowEntity followEntity = FollowEntity.builder()
        .fromMember(fromMember)
        .toMember(toMember)
        .build();

    if (existingFollow.isPresent()) {
      followRepository.delete(existingFollow.get());
      return null;
    }
    followRepository.save(followEntity);
    return FollowDto.fromEntity(followEntity);
  }
}
