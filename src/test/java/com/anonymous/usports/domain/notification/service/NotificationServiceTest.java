package com.anonymous.usports.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.notification.dto.NotificationCreateDto;
import com.anonymous.usports.domain.notification.dto.NotificationDto;
import com.anonymous.usports.domain.notification.entity.NotificationEntity;
import com.anonymous.usports.domain.notification.repository.EmitterRepository;
import com.anonymous.usports.domain.notification.repository.NotificationRepository;
import com.anonymous.usports.domain.notification.service.impl.NotificationServiceImpl;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.NotificationEntityType;
import com.anonymous.usports.global.type.NotificationType;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private EmitterRepository emitterRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  private MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("accountName" + id)
        .name("name" + id)
        .email("test@test.com")
        .password("password" + id)
        .phoneNumber("010-1111-2222")
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
        .role(Role.USER)
        .profileOpen(true)
        .build();
  }

  private NotificationEntity createNotification(Long id, MemberEntity member) {
    return NotificationEntity.builder()
        .member(member)
        .type(NotificationType.ALERT)
        .entityType(NotificationEntityType.PARTICIPANT)
        .targetEntityId(id + 10000)
        .message("test message" + id)
        .url("/test/" + id)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Nested
  @DisplayName("Notification 리스트 조회")
  class GetNotifications {

    @Test
    @DisplayName("성공")
    void getNotifications() {
      MemberEntity member = createMember(1L);
      List<NotificationEntity> notificationList = new ArrayList<>();
      for (long i = 0; i < 10; i++) {
        notificationList.add(createNotification(10L + i, member));
      }
      List<NotificationEntity> savedNotificationList = new ArrayList<>();
      for (long i = 0; i < 10; i++) {
        NotificationEntity notification = createNotification(10L + i, member);
        notification.readNow();
        savedNotificationList.add(notification);
      }
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(notificationRepository.findByMemberOrderByCreatedAtDesc(member))
          .thenReturn(notificationList);
      when(notificationRepository.saveAll(notificationList))
          .thenReturn(savedNotificationList);

      //when
      List<NotificationDto> notifications =
          notificationService.getNotifications(member.getMemberId());

      //then
      for (NotificationDto n : notifications) {
        assertThat(n.getReadAt()).isNotNull();
      }
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void getNotifications_MEMBER_NOT_FOUND() {
      MemberEntity member = createMember(1L);
      List<NotificationEntity> notificationList = new ArrayList<>();
      for (long i = 0; i < 10; i++) {
        notificationList.add(createNotification(10L + i, member));
      }
      List<NotificationEntity> savedNotificationList = new ArrayList<>();
      for (long i = 0; i < 10; i++) {
        NotificationEntity notification = createNotification(10L + i, member);
        notification.readNow();
        savedNotificationList.add(notification);
      }
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
              notificationService.getNotifications(member.getMemberId()), MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

    }

  }

  @Test
  @DisplayName("알림 생성")
  void testNotify() {
    //given
    MemberEntity memberEntity = createMember(1L);
    NotificationEntity notification = createNotification(10L, memberEntity);
    NotificationCreateDto createDto = NotificationCreateDto.builder()
        .type(notification.getType())
        .entityType(notification.getEntityType())
        .targetEntityId(notification.getTargetEntityId())
        .message(notification.getMessage())
        .url(notification.getUrl())
        .build();

    when(notificationRepository.save(NotificationCreateDto.toEntity(createDto, memberEntity)))
        .thenReturn(notification);
    //when
    NotificationDto result = notificationService.notify(memberEntity, createDto);

    //then
    assertThat(result.getType()).isEqualTo(createDto.getType());
    assertThat(result.getEntityType()).isEqualTo(createDto.getEntityType());
    assertThat(result.getTargetEntityId()).isEqualTo(createDto.getTargetEntityId());
    assertThat(result.getMessage()).isEqualTo(createDto.getMessage());
    assertThat(result.getUrl()).isEqualTo(createDto.getUrl());
  }

  @Nested
  @DisplayName("로그인 시 안읽은 알림 여부 확인")
  class CheckUnreadNotificationAndSetSession {

    @Test
    @DisplayName("성공 - 안읽은 알림 있음")
    void checkUnreadNotificationAndSetSession_is_true() {
      MemberEntity member = createMember(1L);

      HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
      HttpSession session = mock(HttpSession.class);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(notificationRepository.existsByMemberAndReadAtIsNull(member))
          .thenReturn(true);
      when(httpServletRequest.getSession())
          .thenReturn(session);

      //when
      boolean result = notificationService
          .checkUnreadNotificationAndSetSession(1L, httpServletRequest);

      //then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("성공 - 안읽은 알림 없음")
    void checkUnreadNotificationAndSetSession_is_false() {
      MemberEntity member = createMember(1L);

      HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
      HttpSession session = mock(HttpSession.class);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(notificationRepository.existsByMemberAndReadAtIsNull(member))
          .thenReturn(false);
      when(httpServletRequest.getSession())
          .thenReturn(session);

      //when
      boolean result = notificationService
          .checkUnreadNotificationAndSetSession(1L, httpServletRequest);

      //then
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void checkUnreadNotificationAndSetSession_MEMBER_NOT_FOUND() {
      MemberEntity member = createMember(1L);

      HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
      HttpSession session = mock(HttpSession.class);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException exception =
          catchThrowableOfType(() ->
                  notificationService
                      .checkUnreadNotificationAndSetSession(1L, httpServletRequest),
              MemberException.class);

      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

  }

}