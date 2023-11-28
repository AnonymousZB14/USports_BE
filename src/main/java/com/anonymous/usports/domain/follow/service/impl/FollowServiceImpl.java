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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;


  @Override
  public FollowDto followMember(Long fromMemberId, Long toMemberId) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(()->new MyException(ErrorCode.MEMBER_NOT_FOUND));

    MemberEntity toMember = memberRepository.findById(toMemberId)
        .orElseThrow(()->new MyException(ErrorCode.MEMBER_NOT_FOUND));


    FollowEntity followEntity = followRepository.save(FollowEntity.builder()
        .fromMember(fromMember)
        .toMember(toMember)
        .build());

    return FollowDto.fromEntity(followEntity);
  }

  @Override
  public FollowDto deleteFollow(Long followId, Long memberId) {
    FollowEntity follow = followRepository.findById(followId)
        .orElseThrow(()->new FollowException(ErrorCode.FOLLOW_NOT_FOUND));
    if(!follow.getFromMember().getMemberId().equals(memberId)){
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
    followRepository.delete(follow);

    return FollowDto.fromEntity(follow);
  }
}
