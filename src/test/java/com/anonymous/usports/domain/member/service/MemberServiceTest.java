package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.dto.MemberLogin;
import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.dto.MemberWithdraw;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.impl.MailServiceImpl;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.constant.ResponseConstant;
import com.anonymous.usports.global.constant.TokenConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
import com.anonymous.usports.global.redis.auth.repository.AuthRedisRepository;
import com.anonymous.usports.global.redis.token.repository.TokenRepository;
import com.anonymous.usports.global.type.Gender;
import com.anonymous.usports.global.type.Role;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private InterestedSportsRepository interestedSportsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SportsRepository sportsRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private AuthRedisRepository authRedisRepository;

    @Mock
    private MailServiceImpl mailService;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Builder
    private static MemberEntity member(Long memberId, String accountName, String name, String email,
                               String password, String phoneNumber, LocalDate birthDate,
                               Gender gender, String profileContent, String profileImage,
                               LocalDateTime emailAuthAt, String addrCity, String addrDistrict,
                               boolean profileOpen, Role role) {
        return MemberEntity.builder()
                .memberId(memberId)
                .accountName(accountName)
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .gender(gender)
                .profileContent(profileContent)
                .profileImage(profileImage)
                .emailAuthAt(emailAuthAt)
                .addrCity(addrCity)
                .addrDistrict(addrDistrict)
                .profileOpen(profileOpen)
                .role(role)
                .build();
    }

    @Builder
    private static SportsEntity sports(Long sportsId, String sportsName) {
        return SportsEntity.builder()
                .sportsId(sportsId)
                .sportsName(sportsName)
                .build();
    }

    @Builder
    private static InterestedSportsEntity interestedSports(Long interestedSportsId,
                                                           MemberEntity member,
                                                           SportsEntity sports
                                                           ) {
        return InterestedSportsEntity.builder()
                .interestedSportsId(interestedSportsId)
                .memberEntity(member)
                .sports(sports)
                .build();
    }


    @Nested
    @DisplayName("첫 회원가입")
    class RegisterMember {

        @Test
        @DisplayName("회원가입 성공")
        void successFirstRegisterMember() {
            //given
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String password = passwordEncoder.encode("abcd1234!");

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", password,
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

            MemberRegister.Request request = MemberRegister.Request.builder()
                    .accountName("joons")
                    .name("Je Joon")
                    .email("joons@gmail.com")
                    .password(password)
                    .phoneNumber("010-1234-1234")
                    .birthDate(birthDate)
                    .gender(Gender.MALE)
                    .profileOpen("open")
                    .build();

            //when
            when(memberRepository.save(MemberRegister.Request.toEntity(request)))
                    .thenReturn(member);

            MemberRegister.Response response = memberService.registerMember(request);

            //then
            assertThat(response.getAccountName()).isEqualTo(member.getAccountName());
            assertThat(response.getEmail()).isEqualTo(member.getEmail());
            assertThat(response.isProfileOpen()).isEqualTo(member.isProfileOpen());
            assertThat(response.getMessage()).isEqualTo(MailConstant.AUTH_EMAIL_SEND);
        }

        @Test
        @DisplayName("회원가입 실패 - 닉네임이 이미 있음")
        void failAccountAlreadyExist() {
            //given
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberRegister.Request request = MemberRegister.Request.builder()
                    .accountName("joons")
                    .name("Je Joon")
                    .email("joons@gmail.com")
                    .password("Aabcd1234!")
                    .phoneNumber("010-1234-1234")
                    .birthDate(birthDate)
                    .gender(Gender.MALE)
                    .profileOpen("open")
                    .build();

            //when
            /*
            DB에 저장이 되는 것이 아니라서, 서비스 메서드 내에서 repository에 접근했을 때
            나타날 수 있는 경우를 찾는다 (여기서는 accountName이 중복이 되면 true를 반환한다.)
            예외처리하기 전에
             */
            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(true);


           /*
            실제 메서드를 실행할 때에, 나타날 수 있는 예외 클래스를 리턴해준다
             */
            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.registerMember(request), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일이 이미 있음")
        void failEmailAlreadyExist() {
            //given
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberRegister.Request request = MemberRegister.Request.builder()
                    .accountName("joons")
                    .name("Je Joon")
                    .email("joons@gmail.com")
                    .password("Aabcd1234!")
                    .phoneNumber("010-1234-1234")
                    .birthDate(birthDate)
                    .gender(Gender.MALE)
                    .profileOpen("open")
                    .build();

            //when
            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(false); // 이건 굳이 없어도 됨
            when(memberRepository.existsByEmail(request.getEmail()))
                    .thenReturn(true);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.registerMember(request), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일이 이미 있음")
        void failPhoneNumberAlreadyExist() {
            //given
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberRegister.Request request = MemberRegister.Request.builder()
                    .accountName("joons")
                    .name("Je Joon")
                    .email("joons@gmail.com")
                    .password("Aabcd1234!")
                    .phoneNumber("010-1234-1234")
                    .birthDate(birthDate)
                    .gender(Gender.MALE)
                    .profileOpen("open")
                    .build();

            //when
            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(false); // 이건 굳이 없어도 됨
            when(memberRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);
            when(memberRepository.existsByPhoneNumber(request.getPhoneNumber()))
                    .thenReturn(true);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.registerMember(request), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login{

        private MemberEntity createMember() {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

            return member;
        }

        @Test
        @DisplayName("로그인 성공")
        void successLogin() {
            //given
            MemberEntity member = createMember();

            MemberLogin.Request request = MemberLogin.Request.builder()
                    .email("joons@gmail.com")
                    .password("abcd1234!")
                    .build();

            //when
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(true);
            log.info("{} -- {}", request.getPassword(), member.getPassword());
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.ofNullable(member));
            log.info("{}", member.getAccountName());

            MemberDto memberDto = memberService.loginMember(request);

            //then
            assertThat(memberDto.getMemberId()).isEqualTo(member.getMemberId());
            assertThat(memberDto.getAccountName()).isEqualTo(member.getAccountName());
            assertThat(memberDto.getEmail()).isEqualTo(member.getEmail());
            assertThat(memberDto.getPhoneNumber()).isEqualTo(member.getPhoneNumber());
        }

        @Test
        @DisplayName("로그인 실패 - 유저가 없음")
        void failLoginUserNotFound() {
            //given
            MemberEntity member = createMember();

            member.setPassword(member.getPassword());

            MemberLogin.Request request = MemberLogin.Request.builder()
                    .email("joons2@gmail.com")
                    .password("abcd1234!")
                    .build();

            //when
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.empty());
            log.info("{}", member.getAccountName());

            MemberException memberException =
                    catchThrowableOfType(() ->
                            memberService.loginMember(request), MemberException.class);

            //then
            assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호가 일치하지 않음")
        void failLoginPasswordNotMatch() {
            //given
            MemberEntity member = createMember();

            member.setPassword(member.getPassword());

            MemberLogin.Request request = MemberLogin.Request.builder()
                    .email("joons@gmail.com")
                    .password("aaaaa23422")
                    .build();

            //when
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(false);
            log.info("{} -- {}", request.getPassword(), member.getPassword());

            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.ofNullable(member));
            log.info("{}", member.getAccountName());

            MemberException memberException =
                    catchThrowableOfType(() ->
                            memberService.loginMember(request), MemberException.class);

            //then
            assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_UNMATCH);
        }
    }


    @Nested
    @DisplayName("로그아웃")
    class Logout {
        private MemberEntity createMember() {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

            return member;
        }

        @Test
        @DisplayName("로그아웃 성공")
        void successLogout() {
            //given
            MemberEntity member = createMember();
            String accessToken = "iAmAccessToken1994";

            //when
            when(tokenRepository.deleteToken(member.getEmail()))
                    .thenReturn(true);

            String logoutMessage = memberService.logoutMember(accessToken, member.getEmail());

            //then
            assertThat(logoutMessage).isEqualTo(TokenConstant.LOGOUT_SUCCESSFUL);
        }

        @Test
        @DisplayName("로그아웃 실패 - Refresh Token이 없음. 즉 로그인 한 적이 없음")
        void failLogoutNoRefreshToken() {
            //given
            MemberEntity member = createMember();
            String accessToken = "iAmAccessToken1994";

            //when
            when(tokenRepository.deleteToken(member.getEmail()))
                    .thenReturn(false);


            String logoutMessage = memberService.logoutMember(accessToken, member.getEmail());

            //then
            assertThat(logoutMessage).isEqualTo(TokenConstant.LOGOUT_NOT_SUCCESSFUL);
        }

    }

    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteMember{

        private MemberEntity createMember(Role role) {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, role);

            return member;
        }

        private MemberEntity createAdmin() {
            LocalDate birthDate = LocalDate.parse("1996-12-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(10L, "ADMIN", "ADMIN LEE", "admin7@gmail.com", "admin01234!",
                    "010-1004-1004", birthDate, Gender.FEMALE, null, null, null, null, null,
                    false, Role.ADMIN);

            return member;
        }

        @Test
        @DisplayName("인증 안 된 회원 탈퇴 성공")
        void successDeleteUNAuthMember() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(true);

            MemberWithdraw.Response response = memberService.deleteMember(memberDto, request, memberId);

            //then
            verify(memberRepository, times(1)).delete(member);
            assertThat(response.getMessage()).isEqualTo(ResponseConstant.MEMBER_DELETE_SUCCESS);
        }

        // 파라미터와 유저DTO의 ID를 비교하는거라 when은 필요가 없음
        @Test
        @DisplayName("인증 안 된 회원 탈퇴 실패 - 다른 유저 ID값")
        void failDeleteUNAuthMemberIdUnmatch() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1111L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_ID_UNMATCH);
        }

        @Test
        @DisplayName("인증 안 된 회원 탈퇴 실패 - 유저가 존재하지 않음")
        void failDeleteUNAuthMemberNotFound() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.empty());

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("인증 안 된 회원 탈퇴 실패 - 비밀번호 일치하지 않음")
        void failDeleteUNAuthMemberPasswordNotMatch() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("bcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(false);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_UNMATCH);
        }

        @Test
        @DisplayName("인증 된 회원 탈퇴 성공")
        void successDeleteUserMember() {
            //given
            MemberEntity member = createMember(Role.USER);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(true);

            MemberWithdraw.Response response = memberService.deleteMember(memberDto, request, memberId);

            //then
            verify(memberRepository, times(1)).delete(member);
            assertThat(response.getMessage()).isEqualTo(ResponseConstant.MEMBER_DELETE_SUCCESS);
        }

        @Test
        @DisplayName("인증 된 회원 탈퇴 실패 - 다른 유저 ID값")
        void failDeleteUserMemberIdUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1111L; // id param 입력값
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_ID_UNMATCH);
        }

        @Test
        @DisplayName("인증 된 회원 탈퇴 실패 - 유저가 존재하지 않음")
        void failDeleteUserMemberNotFound() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.empty());

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("인증 된 회원 탈퇴 실패 - 비밀번호 일치하지 않음")
        void failDDeleteUserMemberPasswordNotMatch() {
            //given
            MemberEntity member = createMember(Role.UNAUTH);
            MemberDto memberDto = MemberDto.fromEntity(member);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("bcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(false);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(memberDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_UNMATCH);
        }

        @Test
        @DisplayName("어드민이 회원 삭제 성공")
        void successDeleteMemberAdmin() {
            //given
            MemberEntity admin = createAdmin();
            MemberEntity member = createMember(Role.UNAUTH);

            MemberDto adminDto = MemberDto.fromEntity(admin);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(true);

            MemberWithdraw.Response response = memberService.deleteMember(adminDto, request, memberId);

            //then
            verify(memberRepository, times(1)).delete(member);
            assertThat(response.getMessage()).isEqualTo(ResponseConstant.MEMBER_DELETE_SUCCESS);
        }

        @Test
        @DisplayName("어드민이 회원 삭제 실패 - 유저가 존재하지 않음")
        void failDeleteAdminNotFound() {
            //given
            MemberEntity admin = createAdmin();

            MemberDto adminDto = MemberDto.fromEntity(admin);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.empty());

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(adminDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("어드민이 회원 삭제 실패 - 비밀번호 일치하지 않음")
        void failDDeleteAdminPasswordNotMatch() {
            //given
            MemberEntity admin = createAdmin();
            MemberEntity member = createMember(Role.USER);

            MemberDto adminDto = MemberDto.fromEntity(admin);
            Long memberId = 1L;
            MemberWithdraw.Request request = new MemberWithdraw.Request("abcd1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));
            when(passwordEncoder.matches(request.getPassword(), member.getPassword()))
                    .thenReturn(false);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.deleteMember(adminDto, request, memberId), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_UNMATCH);
        }
    }
}
