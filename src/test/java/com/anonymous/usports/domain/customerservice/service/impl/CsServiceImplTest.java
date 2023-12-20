package com.anonymous.usports.domain.customerservice.service.impl;

import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.customerservice.repository.CsRepository;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.type.CsStatus;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
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

  private CsEntity createCS(Long id, MemberEntity member,CsStatus csStatus) {
    return CsEntity.builder()
        .csId(id)
        .memberEntity(member)
        .title("this is title " + id)
        .content("this is content " + id)
        .registeredAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .csStatus(csStatus)
        .build();
  }





}
