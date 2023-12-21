package com.anonymous.usports.domain.customerservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anonymous.usports.domain.customerservice.dto.ChangeStatusDto;
import com.anonymous.usports.domain.customerservice.dto.CsDto;
import com.anonymous.usports.domain.customerservice.dto.CsListDto;
import com.anonymous.usports.domain.customerservice.dto.DeleteCS;
import com.anonymous.usports.domain.customerservice.dto.RegisterCS;
import com.anonymous.usports.domain.customerservice.dto.UpdateCS;
import com.anonymous.usports.domain.customerservice.entity.CsEntity;
import com.anonymous.usports.domain.customerservice.repository.CsRepository;
import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.global.constant.CsConstant;
import com.anonymous.usports.global.exception.CsException;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.type.CsStatus;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import java.time.LocalDate;
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

    @Test
    @DisplayName("유저 신고글 등록 실패 - 없는 유저")
    void failRegisterCsUSER() {
      //given
      String title = "제목입니다";
      String content = "내용입니다";

      RegisterCS.Request request = RegisterCS.Request.builder()
          .title(title)
          .content(content)
          .build();

      MemberEntity member = createMember(1L, Role.USER);

      //when
      when(memberRepository.findById(1L)).thenReturn(Optional.empty());

      MemberException response = catchThrowableOfType(() ->
          csService.registerCs(request, MemberDto.fromEntity(member)),
          MemberException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("신고글 삭제")
  class deleteCS{

    @Test
    @DisplayName("성공 - 유저 삭제 성공")
    void deleteSuccessUSER() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user = createMember(1L, Role.USER);
      CsEntity cs = createCS(11L, title, content, user, CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(user.getMemberId())).thenReturn(Optional.of(user));

      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      DeleteCS.Response response = csService.deleteCs(11L, MemberDto.fromEntity(user));

      //then
      verify(csRepository, times(1)).delete(cs);
      assertThat(response.getCsId()).isEqualTo(11L);
      assertThat(response.getMessage()).isEqualTo(CsConstant.SUCCESSFULLY_DELETED);
    }

    @Test
    @DisplayName("성공 - 어드민 다른 게시물 삭제")
    void deleteSuccessADMINOtherCs() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity admin = createMember(123L, Role.ADMIN);

      MemberEntity user = createMember(1L, Role.USER);
      CsEntity cs = createCS(11L, title, content, user, CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(admin.getMemberId())).thenReturn(Optional.of(admin));

      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      DeleteCS.Response response = csService.deleteCs(11L, MemberDto.fromEntity(admin));

      //then
      verify(csRepository, times(1)).delete(cs);
      assertThat(response.getCsId()).isEqualTo(11L);
      assertThat(response.getMessage()).isEqualTo(CsConstant.SUCCESSFULLY_DELETED);
    }

    @Test
    @DisplayName("성공 - 어드민 자신 게시물 삭제")
    void deleteSuccessADMIN() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity admin = createMember(123L, Role.ADMIN);

      CsEntity cs = createCS(11L, title, content, admin, CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(admin.getMemberId())).thenReturn(Optional.of(admin));

      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      DeleteCS.Response response = csService.deleteCs(11L, MemberDto.fromEntity(admin));

      //then
      verify(csRepository, times(1)).delete(cs);
      assertThat(response.getCsId()).isEqualTo(11L);
      assertThat(response.getMessage()).isEqualTo(CsConstant.SUCCESSFULLY_DELETED);
    }

    @Test
    @DisplayName("실패 - 유저가 다른 게시물 삭제")
    void failDeleteUSER() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user1 = createMember(1L, Role.USER);

      MemberEntity user2 = createMember(123L, Role.USER);

      CsEntity cs = createCS(11L, title, content, user2, CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(user1.getMemberId())).thenReturn(Optional.of(user1));

      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      CsException response = catchThrowableOfType(() ->
          csService.deleteCs(11L, MemberDto.fromEntity(user1)), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.CS_NOT_WRITTEN_BY_CURRENT_USER);
    }

    @Test
    @DisplayName("실패 - 게시물을 찾을 수 없음")
    void failDeleteCsNotFound() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user1 = createMember(1L, Role.USER);

      CsEntity cs = createCS(11L, title, content, user1, CsStatus.REGISTERED);

      //when
      when(memberRepository.findById(user1.getMemberId())).thenReturn(Optional.of(user1));

      when(csRepository.findById(11L)).thenReturn(Optional.empty());

      CsException response = catchThrowableOfType(() ->
          csService.deleteCs(11L, MemberDto.fromEntity(user1)), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_CS_FOUND);
    }

    @Test
    @DisplayName("실패 - 유저를 찾을 수 없음")
    void failDeleteUserNotFound() {
      //given

      MemberEntity user1 = createMember(1L, Role.USER);

      //when
      when(memberRepository.findById(user1.getMemberId())).thenReturn(Optional.empty());

      MemberException response = catchThrowableOfType(() ->
          csService.deleteCs(11L, MemberDto.fromEntity(user1)), MemberException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("신고글 수정")
  class updateCS{

    @Test
    @DisplayName("성공 - 자신의 신고 수정")
    void successUpdateUSER() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user = createMember(1L, Role.USER);
      CsEntity cs = createCS(11L, title, content, user,CsStatus.REGISTERED);
      CsEntity updateCs = createCS(11L, title + " change", content + " change", user, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      UpdateCS.Response response = csService.updateCs(
          UpdateCS.Request.builder()
              .title(title + " change")
              .content(content + " change")
              .build(), 11L, MemberDto.fromEntity(user));

      //then
      assertThat(response.getContent()).isEqualTo(updateCs.getContent());
      assertThat(response.getTitle()).isEqualTo(updateCs.getTitle());
      assertThat(response.getMessage()).isEqualTo(CsConstant.SUCCESSFULLY_UPDATED);
    }

    @Test
    @DisplayName("실패 - 다른 사람 신고 수정 시도")
    void failUpdateUSERDifCs() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user = createMember(1L, Role.USER);
      MemberEntity user2 = createMember(22L, Role.USER);

      CsEntity cs = createCS(11L, title, content, user2,CsStatus.REGISTERED);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      CsException response = catchThrowableOfType(() ->
          csService.updateCs(
          UpdateCS.Request.builder()
              .title(title + " change")
              .content(content + " change")
              .build(), 11L, MemberDto.fromEntity(user)), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.CS_NOT_WRITTEN_BY_CURRENT_USER);
    }

    @Test
    @DisplayName("실패 - 어드민 다른 사람 신고 수정 시도")
    void failUpdateADMINDifCs() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user = createMember(1L, Role.ADMIN);
      MemberEntity user2 = createMember(22L, Role.USER);

      CsEntity cs = createCS(11L, title, content, user2, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      CsException response = catchThrowableOfType(() ->
          csService.updateCs(
              UpdateCS.Request.builder()
                  .title(title + " change")
                  .content(content + " change")
                  .build(), 11L, MemberDto.fromEntity(user)), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.CS_NOT_WRITTEN_BY_CURRENT_USER);
    }

    @Test
    @DisplayName("실패 - 어드민 다른 사람 신고 수정 시도")
    void failUpdateCsNotFound() {
      //given
      String title = "this is title";
      String content = "this is content";

      MemberEntity user = createMember(1L, Role.ADMIN);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.empty());

      CsException response = catchThrowableOfType(() ->
          csService.updateCs(
              UpdateCS.Request.builder()
                  .title(title + " change")
                  .content(content + " change")
                  .build(), 11L, MemberDto.fromEntity(user)), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_CS_FOUND);
    }
  }

  @Nested
  @DisplayName("신고글 자세히 보기")
  class getCsDetail {

    @Test
    @DisplayName("성공 - 유저 신고글 자세히 보기")
    void successGetCs() {
      //given
      String title = "this is title";
      String content = "this is content";
      MemberEntity member = createMember(1L, Role.USER);

      CsEntity cs = createCS(11L, title, content, member, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      CsDto csDto = csService.getDetailCs(11L);

      //then
      assertThat(csDto.getCsId()).isEqualTo(cs.getCsId());
      assertThat(csDto.getContent()).isEqualTo(cs.getContent());
      assertThat(csDto.getTitle()).isEqualTo(cs.getTitle());
    }

    @Test
    @DisplayName("성공 - 어드민 신고글 자세히 보기")
    void successGetCsADMIN() {
      //given
      String title = "this is title";
      String content = "this is content";
      MemberEntity member = createMember(1L, Role.ADMIN);

      CsEntity cs = createCS(11L, title, content, member, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.of(cs));

      CsDto csDto = csService.getDetailCs(11L);

      //then
      assertThat(csDto.getCsId()).isEqualTo(cs.getCsId());
      assertThat(csDto.getContent()).isEqualTo(cs.getContent());
      assertThat(csDto.getTitle()).isEqualTo(cs.getTitle());
    }

    @Test
    @DisplayName("실패 - 신고글 ID가 없음")
    void failGetCs() {
      //given

      //when
      when(csRepository.findById(11L)).thenReturn(Optional.empty());

      CsException response = catchThrowableOfType(()->csService.getDetailCs(11L),
          CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_CS_FOUND);
    }

  }

  @Nested
  @DisplayName("신고글 리스트 보기")
  class getCsList {

    @Test
    @DisplayName("성공 - 신고글 리스트 보기")
    void successGetList() {
      //given
      MemberEntity member = createMember(1L, Role.USER);
      List<CsEntity> csList = new ArrayList<>();
      int page = 1;


      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, member, CsStatus.REGISTERED));
      }

      //when
      when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

      when(csRepository.findAllByMemberEntityOrderByUpdatedAtDesc(member,
          PageRequest.of(page - 1, 20)))
          .thenReturn(new PageImpl<>(csList));

      CsListDto response = csService.getCsList(MemberDto.fromEntity(member), page);

      //then
      assertThat(response.getPageNum()).isEqualTo(page);
      assertThat(response.getAllElementsCount()).isEqualTo(20);

      int id = 20;
      for (CsDto cs : response.getCsList()) {
        assertThat(cs.getCsId()).isEqualTo(id++);
      }
    }

    @Test
    @DisplayName("실패 - 없는 페이지")
    void failGetListNoPage() {
      //given
      MemberEntity member = createMember(1L, Role.USER);
      List<CsEntity> csList = new ArrayList<>();
      int page = 3;


      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, member, CsStatus.REGISTERED));
      }

      //when
      when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

      when(csRepository.findAllByMemberEntityOrderByUpdatedAtDesc(member,
          PageRequest.of(page - 1, 20)))
          .thenReturn(new PageImpl<>(csList));

      CsException response = catchThrowableOfType(() ->
          csService.getCsList(MemberDto.fromEntity(member), page),
          CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.PAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - 맴버를 찾을 수 없음")
    void failGetListNoMember() {
      //given
      MemberEntity member = createMember(1L, Role.USER);
      int page = 3;

      //when
      when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.empty());

      MemberException response = catchThrowableOfType(() ->
              csService.getCsList(MemberDto.fromEntity(member), page),
          MemberException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("신고글 보기 - 어드민")
  class getCsListAdmin{
    @Test
    @DisplayName("성공 - 어드민 신고글 리스트 가져오기, 모두 null")
    void successGetCsListAdmin() {
      //given
      MemberEntity admin = createMember(1L, Role.ADMIN);
      MemberEntity user = createMember(11L, Role.USER);

      String email = null;
      String statusNum = null;
      int page = 1;

      List<CsEntity> csList = new ArrayList<>();

      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user, CsStatus.REGISTERED));
      }

      //when
      when(csRepository.findAllByConditionsFromAdmin(
          null, null, PageRequest.of(page - 1, 20)
      )).thenReturn(new PageImpl<>(csList));

      CsListDto response = csService.getCsListAdmin(
          MemberDto.fromEntity(admin),null,null,1);

      //then
      assertThat(response.getPageNum()).isEqualTo(page);
      assertThat(response.getAllElementsCount()).isEqualTo(20);

      int id = 20;
      for (CsDto cs : response.getCsList()) {
        assertThat(cs.getCsId()).isEqualTo(id++);
      }
    }

    @Test
    @DisplayName("성공 - 어드민 신고글 리스트 가져오기, 모두 email")
    void successGetCsListAdminEmail() {
      //given
      MemberEntity admin = createMember(1L, Role.ADMIN);
      MemberEntity user = createMember(11L, Role.USER);
      MemberEntity user2 = createMember(111L, Role.USER);

      String email = user2.getEmail();
      String statusNum = null;
      int page = 1;

      List<CsEntity> csList = new ArrayList<>();
      List<CsEntity> csList2 = new ArrayList<>();

      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user, CsStatus.REGISTERED));
      }

      for (int i = 40; i < 60; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user2, CsStatus.REGISTERED));
      }


      for (int i = 40; i < 60; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList2.add(createCS(Long.valueOf(i), title,content, user2, CsStatus.REGISTERED));
      }

      //when

      when(memberRepository.findByEmail(email)).thenReturn(Optional.of(user2));

      when(csRepository.findAllByConditionsFromAdmin(
          user2, null, PageRequest.of(page - 1, 20)
      )).thenReturn(new PageImpl<>(csList2));

      CsListDto response = csService.getCsListAdmin(
          MemberDto.fromEntity(admin), email,null,1);

      //then
      assertThat(response.getPageNum()).isEqualTo(page);
      assertThat(response.getAllElementsCount()).isEqualTo(20);

      int id = 40;
      for (CsDto cs : response.getCsList()) {
        log.info("{}", cs.getCsId());
        assertThat(cs.getCsId()).isEqualTo(id++);
      }
    }

    @Test
    @DisplayName("성공 - 어드민 신고글 리스트 가져오기, 모두 email, statusNum")
    void successGetCsListAdminEmailStatusNum() {
      //given
      MemberEntity admin = createMember(1L, Role.ADMIN);
      MemberEntity user = createMember(11L, Role.USER);
      MemberEntity user2 = createMember(111L, Role.USER);

      String email = user2.getEmail();
      String statusNum = "1";
      int page = 1;

      List<CsEntity> csList = new ArrayList<>();
      List<CsEntity> csList2 = new ArrayList<>();

      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user, CsStatus.REGISTERED));
      }

      for (int i = 40; i < 60; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user2, CsStatus.REGISTERED));
      }


      for (int i = 40; i < 60; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList2.add(createCS(Long.valueOf(i), title,content, user2, CsStatus.REGISTERED));
      }

      //when

      when(memberRepository.findByEmail(email)).thenReturn(Optional.of(user2));

      when(csRepository.findAllByConditionsFromAdmin(
          user2, CsStatus.REGISTERED, PageRequest.of(page - 1, 20)
      )).thenReturn(new PageImpl<>(csList2));

      CsListDto response = csService.getCsListAdmin(
          MemberDto.fromEntity(admin), email, statusNum,1);

      //then
      assertThat(response.getPageNum()).isEqualTo(page);
      assertThat(response.getAllElementsCount()).isEqualTo(20);

      int id = 40;
      for (CsDto cs : response.getCsList()) {
        log.info("{}", cs.getCsId());
        assertThat(cs.getCsId()).isEqualTo(id++);
      }
    }

    @Test
    @DisplayName("실패 - 페이지를 찾을 수 없음 ")
    void failGetCsListAdminPageNotFound() {
      //given
      MemberEntity admin = createMember(1L, Role.ADMIN);
      MemberEntity user = createMember(11L, Role.USER);

      int page = 2;

      List<CsEntity> csList = new ArrayList<>();

      for (int i = 20; i < 40; i ++) {
        String title = "this is title " + i;
        String content = "this is content" + i;

        csList.add(createCS(Long.valueOf(i), title,content, user, CsStatus.REGISTERED));
      }

      //when
      when(csRepository.findAllByConditionsFromAdmin(
          null, null, PageRequest.of(page - 1, 20)
      )).thenReturn(new PageImpl<>(csList));

      CsException response = catchThrowableOfType(() -> csService.getCsListAdmin(
          MemberDto.fromEntity(admin),null,null,page), CsException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.PAGE_NOT_FOUND);

    }

    @Test
    @DisplayName("실패 - 어드민이 아님")
    void failGetCsListAdminNotAdmin() {
      //given
      MemberEntity user = createMember(11L, Role.USER);

      int page = 1;

      //when

      MemberException response = catchThrowableOfType(() -> csService.getCsListAdmin(
          MemberDto.fromEntity(user),null,null, page), MemberException.class);

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_ERROR);

    }
  }

  @Nested
  @DisplayName("신고글 상태 바꾸기")
  class changeCsStatus{
    @Test
    @DisplayName("성공 - 신고글 상태 바꾸기 성공")
    void successChangeCs() {
      //given
      String title = "this is title";
      String content = "this is content";

      ChangeStatusDto.Request request = ChangeStatusDto.Request.builder()
          .statusNum(2)
          .build();

      MemberEntity member = createMember(1L, Role.ADMIN);

      CsEntity cs = createCS(111L, title, content, member, CsStatus.REGISTERED);
      CsEntity csAfter = createCS(111L, title, content, member, CsStatus.ING);

      //when
      when(csRepository.findById(111L)).thenReturn(Optional.of(cs));

      ChangeStatusDto.Response response =
          csService.changeCsStatus(request, 111L, MemberDto.fromEntity(member));

      //then
      assertThat(response.getCsStatus()).isEqualTo(csAfter.getCsStatus().getDescription());
      assertThat(response.getMessage()).isEqualTo(CsConstant.CS_STATUS_SUCCESSFULLY_CHANGED);
    }

    @Test
    @DisplayName("실패 - 신고글 상태 입력 실패")
    void failChangeCsWrongInput() {
      //given
      String title = "this is title";
      String content = "this is content";

      ChangeStatusDto.Request request = ChangeStatusDto.Request.builder()
          .statusNum(5)
          .build();

      MemberEntity member = createMember(1L, Role.ADMIN);

      CsEntity cs = createCS(111L, title, content, member, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(111L)).thenReturn(Optional.of(cs));

      CsException response = catchThrowableOfType(
          () -> csService.changeCsStatus(request, 111L, MemberDto.fromEntity(member)),
          CsException.class
      );

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.INPUT_VALUE_NOT_EXIST_FOR_CS_STATUS);
    }

    @Test
    @DisplayName("실패 - 신고글 찾을 수 없음")
    void failChangeCsNotFound() {
      //given
      String title = "this is title";
      String content = "this is content";

      ChangeStatusDto.Request request = ChangeStatusDto.Request.builder()
          .statusNum(5)
          .build();

      MemberEntity member = createMember(1L, Role.ADMIN);

      CsEntity cs = createCS(111L, title, content, member, CsStatus.REGISTERED);

      //when
      when(csRepository.findById(111L)).thenReturn(Optional.empty());

      CsException response = catchThrowableOfType(
          () -> csService.changeCsStatus(request, 111L, MemberDto.fromEntity(member)),
          CsException.class
      );

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_CS_FOUND);
    }

    @Test
    @DisplayName("실패 - 어드민이 아님")
    void failChangeCsNotAdmin() {
      //given
      ChangeStatusDto.Request request = ChangeStatusDto.Request.builder()
          .statusNum(5)
          .build();

      MemberEntity member = createMember(1L, Role.USER);

      //when

      MemberException response = catchThrowableOfType(
          () -> csService.changeCsStatus(request, 111L, MemberDto.fromEntity(member)),
          MemberException.class
      );

      //then
      assertThat(response.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY_ERROR);
    }

  }

}
