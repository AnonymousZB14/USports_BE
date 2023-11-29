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

    Optional<FollowEntity> existingFollow = followRepository.findByFromMemberAndToMemberAndConsentDateIsNotNull(
        fromMember, toMember);

    if (existingFollow.isPresent()) {
      followRepository.delete(existingFollow.get());
      return FollowResponse.Response(null, ResponseConstant.DELETE_FOLLOW);
    }
    FollowEntity follow = followRepository.save(FollowEntity.builder()
        .fromMember(fromMember)
        .toMember(toMember)
        .consentDate(null)
        .build());
    return FollowResponse.Response(follow.getFollowId(), ResponseConstant.REGISTER_FOLLOW);
  }

  /**
   * 팔로우와 관련된 리스트를 출력하는 메서드 listType에 따라 다른 리스트를 출력
   * FOLLOWING:내가 FOLLOW 하는 리스트
   * REQUESTED_FOLLOW:나에게 들어온 FOLLOW 신청 리스트
   * FOLLOWER: 나를 FOLLOW 하는 리스트
   */
  @Override
  public FollowListDto getFollowList(FollowListType type, int page, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MyException(ErrorCode.MEMBER_NOT_FOUND));

    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);

    Page<FollowEntity> findPage;
    if (type == FollowListType.FOLLOWING) {
      findPage = followRepository.findAllByFromMemberAndConsentDateIsNotNull(member, pageRequest);
    } else if (type == FollowListType.REQUESTED_FOLLOW) {
      findPage = followRepository.findAllByToMemberAndConsentDateIsNull(member, pageRequest);
    }
    findPage = followRepository.findAllByToMemberAndConsentDateIsNotNull(member, pageRequest);

    return FollowListDto.fromEntityPage(findPage);
  }

  /**
   * 팔로우 신청 수락 / 거절 메서드
   */
  @Override
  public FollowResponse manageFollow(Long followId, Long memberId, FollowDecisionType decision) {
    FollowEntity follow = followRepository.findById(followId)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_NOT_FOUND));
    if (!follow.getToMember().equals(memberId)) {
      throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
    }
    if (decision == FollowDecisionType.REFUSE) {
      followRepository.delete(follow);
      return FollowResponse.Response(null, ResponseConstant.REFUSE_FOLLOW);
    }
    follow.setConsentDate(LocalDateTime.now());
    followRepository.save(follow);
    return FollowResponse.Response(follow.getFollowId(), ResponseConstant.ACCEPT_FOLLOW);
  }


}
