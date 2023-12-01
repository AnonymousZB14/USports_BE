package com.anonymous.usports.domain.follow.service.impl;

import com.anonymous.usports.domain.follow.dto.FollowListDto;
import com.anonymous.usports.domain.follow.dto.FollowResponse;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.follow.service.FollowService;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.FollowException;
import com.anonymous.usports.global.exception.MyException;
import com.anonymous.usports.global.type.FollowDecisionType;
import com.anonymous.usports.global.type.FollowListType;
import com.anonymous.usports.global.type.FollowStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;

  /**
   * 팔로우 신청, 취소 메서드
   */
  @Override
  public FollowResponse changeFollow(Long fromMemberId, Long toMemberId) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    MemberEntity toMember = memberRepository.findById(toMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    Optional<FollowEntity> existingFollow = followRepository.findByFromMemberAndToMemberAndFollowStatus(
        fromMember, toMember, FollowStatus.ACTIVE);

    if (existingFollow.isPresent()) {
      followRepository.delete(existingFollow.get());
      return FollowResponse.Response(null, ResponseConstant.DELETE_FOLLOW);
    }
    FollowEntity follow = followRepository.save(FollowEntity.builder()
        .fromMember(fromMember)
        .toMember(toMember)
        .build());
    if(toMember.isProfileOpen()){
      follow.setFollowStatus(FollowStatus.ACTIVE);
      follow.setFollowDate(LocalDateTime.now());
    } else {
      follow.setFollowStatus(FollowStatus.WAITING);
    }
    follow = followRepository.save(follow);
    return FollowResponse.Response(follow.getFollowId(), ResponseConstant.REGISTER_FOLLOW);
  }

  /**
   * 팔로우와 관련된 리스트를 출력하는 메서드 listType에 따라 다른 리스트를 출력
   * FOLLOWING:내가 FOLLOW 하는 리스트
   * REQUESTED_FOLLOW:나에게 들어온 FOLLOW 신청 리스트
   * FOLLOWER: 나를 FOLLOW 하는 리스트
   */
  @Override
  public FollowListDto getFollowPage(FollowListType type, int page, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);

    Page<FollowEntity> findPage;
    if (type == FollowListType.FOLLOWING) {
      findPage = followRepository.findAllByFromMemberAndFollowStatusOrderByFollowDateDesc(member, FollowStatus.ACTIVE,pageRequest);
    } else if (type == FollowListType.REQUESTED_FOLLOW) {
      findPage = followRepository.findAllByToMemberAndFollowStatus(member, FollowStatus.WAITING ,pageRequest);
    } else {
      findPage = followRepository.findAllByToMemberAndFollowStatusOrderByFollowDateDesc(member, FollowStatus.ACTIVE, pageRequest);
    }
    return FollowListDto.fromEntityPage(findPage);
  }

  /**
   * 팔로우 신청 수락 / 거절 메서드
   */
  @Override
  public FollowResponse manageFollow(Long fromMemberId, Long toMemberId, FollowDecisionType decision) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    MemberEntity toMember = memberRepository.findById(toMemberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));
    FollowEntity follow = followRepository.findByFromMemberAndToMember(fromMember,toMember)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_NOT_FOUND));
    if (!follow.getFromMember().equals(fromMember)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
    if(follow.getFollowStatus()==FollowStatus.ACTIVE){
      throw new FollowException(ErrorCode.UNABLE_MANAGE_FOLLOW);
    }
    if (decision == FollowDecisionType.REFUSE) {
      followRepository.delete(follow);
      return FollowResponse.Response(null, ResponseConstant.REFUSE_FOLLOW);
    }
    follow.setFollowStatus(FollowStatus.ACTIVE);
    follow.setFollowDate(LocalDateTime.now());
    followRepository.save(follow);
    return FollowResponse.Response(follow.getFollowId(), ResponseConstant.ACCEPT_FOLLOW);
  }
}
