package com.anonymous.usports.domain.follow.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.follow.dto.FollowListDto;
import com.anonymous.usports.domain.follow.dto.FollowResponse;
import com.anonymous.usports.domain.follow.entity.FollowEntity;
import com.anonymous.usports.domain.follow.repository.FollowRepository;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.constant.NumberConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.FollowException;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.FollowDecisionType;
import com.anonymous.usports.global.type.FollowListType;
import com.anonymous.usports.global.type.FollowStatus;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@Slf4j
class FollowServiceImplTest {

  @Mock
  private FollowRepository followRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private FollowServiceImpl followService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("testAccountName" + id)
        .name("testName" + id)
        .email("test@test" + id + ".com")
        .password("password" + id)
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();
  }

  private FollowEntity createFollow(Long id, MemberEntity fromMember, MemberEntity toMember) {
    return FollowEntity.builder()
        .followId(id)
        .fromMember(fromMember)
        .toMember(toMember)
        .followDate(LocalDateTime.now())
        .followStatus(FollowStatus.ACTIVE)
        .build();
  }


  @Nested
  @DisplayName("follow 신청/취소")
  class changeFollow {

    @Test
    @DisplayName("성공 - 기존 Follow가 없을 때 Follow 신청")
    void success_Follow() {

      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(Optional.empty());

      FollowResponse response = followService.changeFollow(fromMember.getMemberId(),
          toMember.getMemberId());

      verify(followRepository, times(1)).save(any(FollowEntity.class));

      assertEquals(response.getMessage(), ResponseConstant.REGISTER_FOLLOW);
    }

    @Test
    @DisplayName("성공 - 기존 FollowActive가 존재할 때 Follow 취소")
    void success_DeleteFollowActive() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);
      toMember.setProfileOpen(false);
      FollowEntity follow = createFollow(10L, fromMember, toMember);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(Optional.of(follow));

      FollowResponse response = followService.changeFollow(fromMember.getMemberId(),
          toMember.getMemberId());

      verify(followRepository, times(1)).delete(follow);

      assertEquals(response.getMessage(), ResponseConstant.DELETE_FOLLOW);
    }


    @Test
    @DisplayName("성공 - 기존 FollowWaiting이 존재할 때 Follow 취소")
    void success_DeleteFollowWaiting() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);
      toMember.setProfileOpen(false);
      FollowEntity follow = createFollow(10L, fromMember, toMember);
      follow.setFollowStatus(FollowStatus.WAITING);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.of(toMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(Optional.of(follow));

      FollowResponse response = followService.changeFollow(fromMember.getMemberId(),
          toMember.getMemberId());

      verify(followRepository, times(1)).delete(follow);

      assertEquals(response.getMessage(), ResponseConstant.CANCEL_FOLLOW_REQUEST);
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void fail_FollowMemberNotFound() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity toMember = createMember(2L);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
                  followService.changeFollow(fromMember.getMemberId(), toMember.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - SELF_FOLLOW_NOT_ALLOWED")
    void fail_SelfFollowNotAllowed() {
      MemberEntity fromMember = createMember(1L);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));

      FollowException exception =
          catchThrowableOfType(() ->
                  followService.changeFollow(fromMember.getMemberId(), fromMember.getMemberId()),
              FollowException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }
  }

  @Nested
  @DisplayName("Follow 페이지 얻기")
  class getFollowPage {

    @Test
    @DisplayName("성공 - FOLLOWING 리스트 가져오기")
    void success_GetFollowingPage() {
      MemberEntity member = createMember(1L);
      List<MemberEntity> members = new ArrayList<>();

      Long numberOfMembers = 15L;

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        members.add(createMember(i));
      }

      List<FollowEntity> followEntities = new ArrayList<>();

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        followEntities.add(createFollow(i * 100, member, createMember(i)));
      }

      int page = 2;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), followEntities.size());
      List<FollowEntity> paginatedFollowEntities = followEntities.subList(start, end);
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(followRepository.findAllByFromMemberAndFollowStatusOrderByFollowDateDesc(member,
          FollowStatus.ACTIVE, pageRequest))
          .thenReturn(new PageImpl<>(paginatedFollowEntities, pageRequest, followEntities.size()));

      FollowListDto result =
          followService.getFollowPage(FollowListType.FOLLOWING, page, member.getMemberId());

      assertNotNull(result);
      assertEquals(followEntities.size(), result.getTotalElements());
      assertEquals(result.getCurrentPage(), 2);
      assertEquals(result.getTotalPages(), 2);

      for (int i = 0; i < paginatedFollowEntities.size(); i++) {
        assertEquals(paginatedFollowEntities.get(i).getFollowId(),
            result.getList().get(i).getFollowId());
      }
    }

    @Test
    @DisplayName("성공 - FOLLOWER 리스트 가져오기")
    void success_GetFollowerPage() {
      MemberEntity member = createMember(1L);
      List<MemberEntity> members = new ArrayList<>();

      Long numberOfMembers = 15L;

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        members.add(createMember(i));
      }

      List<FollowEntity> followEntities = new ArrayList<>();

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        followEntities.add(createFollow(i * 100, createMember(i), member));
      }

      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), followEntities.size());
      List<FollowEntity> paginatedFollowEntities = followEntities.subList(start, end);

      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(followRepository.findAllByToMemberAndFollowStatusOrderByFollowDateDesc
          (member, FollowStatus.ACTIVE, pageRequest))
          .thenReturn(new PageImpl<>(paginatedFollowEntities, pageRequest, followEntities.size()));

      FollowListDto result =
          followService.getFollowPage(FollowListType.FOLLOWER, page, member.getMemberId());

      assertNotNull(result);
      assertEquals(followEntities.size(), result.getTotalElements());
      assertEquals(result.getCurrentPage(), 1);
      assertEquals(result.getTotalPages(), 2);

      for (int i = 0; i < paginatedFollowEntities.size(); i++) {
        assertEquals(paginatedFollowEntities.get(i).getFollowId(),
            result.getList().get(i).getFollowId());
      }
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void fail_MemberNotFound() {
      MemberEntity member = createMember(1L);
      List<MemberEntity> members = new ArrayList<>();

      Long numberOfMembers = 15L;

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        members.add(createMember(i));
      }

      List<FollowEntity> followEntities = new ArrayList<>();

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        followEntities.add(createFollow(i * 100, member, createMember(i)));
      }

      int page = 2;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), followEntities.size());
      List<FollowEntity> paginatedFollowEntities = followEntities.subList(start, end);

      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
                  followService.getFollowPage(FollowListType.FOLLOWING, page,
                      member.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);

    }
  }

  @Nested
  @DisplayName("Follow 신청 받은 리스트")
  class RequestesFollowList {

    @Test
    @DisplayName("성공 - Follow 신청 받은 리스트")
    void success_RequestedFollowList() {
      MemberEntity loginMember = createMember(1L);
      loginMember.setProfileOpen(false);
      List<MemberEntity> members = new ArrayList<>();

      Long numberOfMembers = 15L;

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        members.add(createMember(i));
      }

      List<FollowEntity> followEntities = new ArrayList<>();

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        FollowEntity follow = createFollow(i * 100, createMember(i), loginMember);
        follow.setFollowStatus(FollowStatus.WAITING);
        followEntities.add(follow);
      }

      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), followEntities.size());

      List<FollowEntity> paginatedFollowEntities = followEntities.subList(start, end);

      when(memberRepository.findById(1L)).thenReturn(Optional.of(loginMember));
      when(followRepository.findAllByToMemberAndFollowStatus(loginMember, FollowStatus.WAITING,
          pageRequest))
          .thenReturn(new PageImpl<>(paginatedFollowEntities, pageRequest, followEntities.size()));

      FollowListDto result = followService.getRequestedFollowList(page, loginMember.getMemberId());

      assertNotNull(result);
      assertEquals(followEntities.size(), result.getTotalElements());
      assertEquals(result.getCurrentPage(), 1);
      assertEquals(result.getTotalPages(), 2);

      for (int i = 0; i < paginatedFollowEntities.size(); i++) {
        assertEquals(paginatedFollowEntities.get(i).getFollowId(),
            result.getList().get(i).getFollowId());
      }
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void fail_MemberNotFound() {
      MemberEntity loginMember = createMember(1L);
      loginMember.setProfileOpen(false);
      List<MemberEntity> members = new ArrayList<>();

      Long numberOfMembers = 15L;

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        members.add(createMember(i));
      }

      List<FollowEntity> followEntities = new ArrayList<>();

      for (Long i = 2L; i <= numberOfMembers + 1; i++) {
        FollowEntity follow = createFollow(i * 100, createMember(i), loginMember);
        follow.setFollowStatus(FollowStatus.WAITING);
        followEntities.add(follow);
      }

      int page = 1;
      PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.PAGE_SIZE_DEFAULT);
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageRequest.getPageSize()), followEntities.size());

      List<FollowEntity> paginatedFollowEntities = followEntities.subList(start, end);


      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
                  followService.getRequestedFollowList(page, loginMember.getMemberId()),
              MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);

    }
  }

  @Nested
  @DisplayName("Follow 수락/거절")
  class manageFollow {

    @Test
    @DisplayName("성공 - Follow 신청 수락")
    void success_AcceptFollowRequest() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity loginMember = createMember(2L);
      loginMember.setProfileOpen(false);
      FollowEntity follow = createFollow(100L, fromMember, loginMember);
      follow.setFollowStatus(FollowStatus.WAITING);
      FollowDecisionType decision = FollowDecisionType.ACCEPT;

      when(memberRepository.findById(1L)).thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L)).thenReturn(Optional.of(loginMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, loginMember))
          .thenReturn(Optional.of(follow));

      FollowResponse response = followService.manageFollow(fromMember.getMemberId(),
          loginMember.getMemberId(), decision);

      verify(followRepository, times(1)).save(follow);
      assertEquals(ResponseConstant.ACCEPT_FOLLOW, response.getMessage());
      assertNotNull(response.getFollowId());
    }

    @Test
    @DisplayName("성공 - Follow 신청 거절")
    void success_RefuseFollowRequest() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity loginMember = createMember(2L);
      loginMember.setProfileOpen(false);
      FollowEntity follow = createFollow(100L, fromMember, loginMember);
      follow.setFollowStatus(FollowStatus.WAITING);
      FollowDecisionType decision = FollowDecisionType.REFUSE;

      when(memberRepository.findById(1L)).thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L)).thenReturn(Optional.of(loginMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, loginMember))
          .thenReturn(Optional.of(follow));

      FollowResponse response = followService.manageFollow(fromMember.getMemberId(),
          loginMember.getMemberId(), decision);

      verify(followRepository, times(1)).delete(follow);
      assertEquals(ResponseConstant.REFUSE_FOLLOW, response.getMessage());
      assertNull(response.getFollowId());
    }

    @Test
    @DisplayName("실패 - MEMBER_NOT_FOUND")
    void fail_MemberNotFound() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity loginMember = createMember(2L);
      FollowDecisionType decision = FollowDecisionType.REFUSE;

      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException exception =
          catchThrowableOfType(() ->
              followService.manageFollow(fromMember.getMemberId(), loginMember.getMemberId(),
                  decision), MemberException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - FOLLOW_NOT_FOUND")
    void fail_FollowNotFound() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity loginMember = createMember(2L);
      loginMember.setProfileOpen(false);
      FollowEntity follow = createFollow(100L, fromMember, loginMember);
      follow.setFollowStatus(FollowStatus.WAITING);
      FollowDecisionType decision = FollowDecisionType.ACCEPT;

      when(memberRepository.findById(1L)).thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L)).thenReturn(Optional.of(loginMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, loginMember))
          .thenReturn(Optional.empty());
      FollowException exception =
          catchThrowableOfType(() ->
              followService.manageFollow(fromMember.getMemberId(), loginMember.getMemberId(),
                  decision), FollowException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.FOLLOW_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - UNABLE_MANAGE_FOLLOW")
    void fail_UnableManageFollow() {
      MemberEntity fromMember = createMember(1L);
      MemberEntity loginMember = createMember(2L);
      loginMember.setProfileOpen(false);
      FollowEntity follow = createFollow(100L, fromMember, loginMember);
      follow.setFollowStatus(FollowStatus.ACTIVE);
      FollowDecisionType decision = FollowDecisionType.ACCEPT;

      when(memberRepository.findById(1L)).thenReturn(Optional.of(fromMember));
      when(memberRepository.findById(2L)).thenReturn(Optional.of(loginMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, loginMember))
          .thenReturn(Optional.of(follow));
      FollowException exception =
          catchThrowableOfType(() ->
              followService.manageFollow(fromMember.getMemberId(), loginMember.getMemberId(),
                  decision), FollowException.class);
      assertEquals(exception.getErrorCode(), ErrorCode.UNABLE_MANAGE_FOLLOW);
    }


  }
}