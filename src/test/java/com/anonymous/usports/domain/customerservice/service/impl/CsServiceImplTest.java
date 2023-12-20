package com.anonymous.usports.domain.customerservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.customerservice.repository.CsRepository;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.type.CsStatus;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CsServiceImplTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private CsRepository csRepository;

  @InjectMocks
  private CsServiceImpl csService;

  private MemberEntity createMember(Long id, Role role) {
    return MemberEntity.builder()
        .memberId(id)
        .accountName("accountName" + id)
        .name("name" + id)
        .email("test@test.com")
        .password("password" + id)
        .phoneNumber("010-1111-2222")
        .birthDate(LocalDate.now())
        .gender(Gender.MALE)
        .role(role)
        .profileOpen(true)
        .build();
  }

  private CsEntity createCS(Long id, String title, String content, MemberEntity member,CsStatus csStatus) {
    return CsEntity.builder()
        .csId(id)
        .memberEntity(member)
        .title(title)
        .content(content)
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .csStatus(csStatus)
        .build();
  }

  @Nested
  @DisplayName("신고글 등록")
  class registerCS {

    @Test
    @DisplayName("유저 신고글 등록 성공")
    void successRegisterCsUSER() {
      //given
      String title = "제목입니다";
      String content = "내용입니다";

      RegisterCS.Request request = RegisterCS.Request.builder()
          .title(title)
          .content(content)
          .build();

      MemberEntity member = createMember(1L, Role.USER);

      CsEntity cs = createCS(5L, title, content,  member,  CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(csRepository.save(CsEntity.builder()
              .title(request.getTitle())
              .content(request.getContent())
              .memberEntity(member)
              .csStatus(CsStatus.REGISTERED)
          .build())).thenReturn(cs);

      RegisterCS.Response response =
          csService.registerCs(request, MemberDto.fromEntity(member));

      //then
      assertThat(response.getContent()).isEqualTo(content);
      assertThat(response.getTitle()).isEqualTo(title);
      assertThat(response.getStatus()).isEqualTo(CsStatus.REGISTERED.getDescription());
    }

    @Test
    @DisplayName("어드민 신고글 기록 성공")
    void successRegisterCsADMIN() {
      //given
      String title = "제목입니다";
      String content = "내용입니다";

      RegisterCS.Request request = RegisterCS.Request.builder()
          .title(title)
          .content(content)
          .build();

      MemberEntity member = createMember(1L, Role.ADMIN);

      CsEntity cs = createCS(5L, title, content,  member,  CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
      when(csRepository.save(CsEntity.builder()
          .title(request.getTitle())
          .content(request.getContent())
          .memberEntity(member)
          .csStatus(CsStatus.REGISTERED)
          .build())).thenReturn(cs);

      RegisterCS.Response response =
          csService.registerCs(request, MemberDto.fromEntity(member));

      //then
      assertThat(response.getContent()).isEqualTo(content);
      assertThat(response.getTitle()).isEqualTo(title);
      assertThat(response.getStatus()).isEqualTo(CsStatus.REGISTERED.getDescription());
    }


  }
}
