package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.*;
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
import com.anonymous.usports.global.exception.MyException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Nested
    @DisplayName("회원 수정")
    class UpdateMember{

        private MemberEntity createMember(Role role) {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, role);

            return member;
        }

        public List<InterestedSportsEntity> createInterestedSport(MemberEntity member, List<SportsEntity> sports) {
            List<InterestedSportsEntity> interestedSportsEntities = new ArrayList<>();

            for (SportsEntity sport : sports) {
                interestedSportsEntities.add(InterestedSportsEntity.builder()
                                .memberEntity(member)
                                .sports(sport)
                        .build());
            }

            return interestedSportsEntities;
        }

        private MemberUpdate.Request createMemberRequest(int emailAuthNumber, String accountName, String email,
                                                         String phoneNumber, String addrCity, String addrDistrict,
                                                         List<Long> sports) {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return MemberUpdate.Request.builder()
                    .emailAuthNumber(emailAuthNumber)
                    .accountName(accountName)
                    .name("joons")
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .birthDate(birthDate)
                    .gender(Gender.FEMALE)
                    .profileContent("content")
                    .profileImage("picture")
                    .addrCity(addrCity)
                    .addrDistrict(addrDistrict)
                    .profileOpen("close")
                    .interestedSports(sports)
                    .build();
        }

        @Test
        @DisplayName("UNAUTH 회원, 수정 성공 후 USER로 전환")
        void successFirstUpdate() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L}));

            MemberEntity member = createMember(Role.UNAUTH);
            MemberUpdate.Request request = createMemberRequest(
                    00000, "joons",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
                    );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            SportsEntity football = sports(1L, "football");
            SportsEntity basketball = sports(2L, "basketball");
            List<SportsEntity> sportsEntities = new ArrayList<>(Arrays.asList(new SportsEntity[]{football, basketball}));

            List<InterestedSportsEntity> interestedSportsEntities = createInterestedSport(member, sportsEntities);

            List<String> interestedSportResult = new ArrayList<>(Arrays.asList(new String[]{"football", "basketball"}));

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            for (int i = 0; i < request.getInterestedSports().size(); i++) {
                when(sportsRepository.findById(request.getInterestedSports().get(i)))
                        .thenReturn(Optional.of(sportsEntities.get(i)));
            }

            when(interestedSportsRepository.saveAll(interestedSportsEntities))
                    .thenReturn(interestedSportsEntities);

            when(interestedSportsRepository.findAllByMemberEntity(member))
                    .thenReturn(interestedSportsEntities);

            MemberUpdate.Response response = memberService.updateMember(request, memberDto, memberId);

            log.info("{} - {}", response.getInterestedSports(), interestedSportResult);
            log.info("{}", response.getRole());
            //then
            assertThat(response.getAccountName()).isEqualTo(request.getAccountName());
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getEmail()).isEqualTo(request.getEmail());
            assertThat(response.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
            assertThat(response.getBirthDate()).isEqualTo(request.getBirthDate());
            assertThat(response.getAddrCity()).isEqualTo(request.getAddrCity());
            assertThat(response.getAddrDistrict()).isEqualTo(request.getAddrDistrict());
            assertThat(response.getInterestedSports()).isEqualTo(interestedSportResult);
            assertThat(response.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("USER 수정 실패, 입력한 스포츠를 찾을 수 없음")
        void failUpdateSportsNotFound() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L, 4L}));

            MemberEntity member = createMember(Role.USER);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "joons",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            List<SportsEntity> sportsEntities = new ArrayList<>();
            sportsEntities.add(sports(1L, "football"));
            sportsEntities.add(sports(2L, "basketball"));
            sportsEntities.add(sports(3L, "rugby"));

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            when(sportsRepository.findById(request.getInterestedSports().get(0)))
                    .thenReturn(Optional.of(sportsEntities.get(0)));

            when(sportsRepository.findById(request.getInterestedSports().get(1)))
                    .thenReturn(Optional.of(sportsEntities.get(1)));

            when(sportsRepository.findById(request.getInterestedSports().get(2)))
                    .thenReturn(Optional.empty());


            MyException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MyException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SPORTS_NOT_FOUND);
        }

        @Test
        @DisplayName("USER 수정 실패, 최소 한 개의 스포츠를 선택해야 함")
        void failUpdateMemberNeedMinSport() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{}));

            MemberEntity member = createMember(Role.USER);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "joons",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            List<SportsEntity> sportsEntities = new ArrayList<>();
            sportsEntities.add(sports(1L, "football"));
            sportsEntities.add(sports(2L, "basketball"));
            sportsEntities.add(sports(3L, "rugby"));

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NEED_AT_LEAST_ONE_SPORTS);
        }

        @Test
        @DisplayName("UNAUTH 회원, 이메일 인증 번호 잘 못 입력")
        void failUpdateMemberAuthUnmatch() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{}));

            MemberEntity member = createMember(Role.UNAUTH);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "joons",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            List<SportsEntity> sportsEntities = new ArrayList<>();
            sportsEntities.add(sports(1L, "football"));
            sportsEntities.add(sports(2L, "basketball"));
            sportsEntities.add(sports(3L, "rugby"));

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            log.info("{}", memberDto.getEmailAuthAt());
            log.info("{}", memberDto.getRole());

            when(authRedisRepository.getEmailAuthNumber(request.getEmail()))
                    .thenReturn(12345);

            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_AUTH_NUMBER_UNMATCH);
        }

        @Test
        @DisplayName("회원, 회원을 찾을 수 없음")
        void failUpdateMemberNotFound() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L}));

            MemberEntity member = createMember(Role.UNAUTH);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "joons",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.empty());


            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("회원, 변경할 닉네임이 이미 존재함")
        void failUpdateMemberAccountAlreadyExist() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L}));

            MemberEntity member = createMember(Role.UNAUTH);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "jjjjj3333",
                    "joons@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(true);

            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("회원, 변경할 이메일이 이미 존재함")
        void failUpdateMemberEmailAlreadyExist() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L}));

            MemberEntity member = createMember(Role.UNAUTH);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "jjjjj3333",
                    "joonstest4@gmail.com", "010-1234-1234",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(false);

            when(memberRepository.existsByEmail(request.getEmail()))
                    .thenReturn(true);

            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("회원, 변경할 핸드폰번호가 이미 존재함")
        void failUpdateMemberPhoneAlreadyExist() {
            //given
            List<Long> sports = new ArrayList<>(Arrays.asList(new Long[]{1L, 2L}));

            MemberEntity member = createMember(Role.UNAUTH);

            MemberUpdate.Request request = createMemberRequest(
                    00000, "jjjjj3333",
                    "joonstest4@gmail.com", "010-1004-1004",
                    "Seoul", "Naro", sports
            );
            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            //when
            when(memberRepository.findById(memberDto.getMemberId()))
                    .thenReturn(Optional.of(member));

            when(memberRepository.existsByAccountName(request.getAccountName()))
                    .thenReturn(false);

            when(memberRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(memberRepository.existsByPhoneNumber(request.getPhoneNumber()))
                    .thenReturn(true);

            MemberException exception = catchThrowableOfType(
                    ()-> memberService.updateMember(request, memberDto, memberId), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }
    
    @Nested
    @DisplayName("비밀번호 수정")
    class UpdatePassword{

        private MemberEntity createMember(Role role) {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, role);

            return member;
        }

        @Test
        @DisplayName("비밀번호 수정 성공")
        void successUpdatePassword() {
            //given
            MemberEntity member = createMember(Role.USER);

            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            PasswordUpdate.Request request = new PasswordUpdate.Request(
                    "abcd1234!", "qwer1234!", "qwer1234!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));

            when(passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
                    .thenReturn(true);

            when(memberRepository.save(member))
                    .thenReturn(member);

            PasswordUpdate.Response response = memberService.updatePassword(request, memberId, memberDto);

            //then
            assertThat(response.getMessage()).isEqualTo(ResponseConstant.PASSWORD_CHANGE_SUCCESS);
        }

        @Test
        @DisplayName("비밀번호 수정 실패 - 새로운 비밀번호와 확인 비밀번호가 다름")
        void failUpdatePasswordNewPasswordUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);

            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            PasswordUpdate.Request request = new PasswordUpdate.Request(
                    "abcd1234!", "qwer1234!", "qwer77777!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));

            when(passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
                    .thenReturn(true);

            MemberException exception = catchThrowableOfType(
                    () -> memberService.updatePassword(request, memberId, memberDto), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NEW_PASSWORD_UNMATCH);
        }

        @Test
        @DisplayName("비밀번호 수정 실패 - 입력한 비밀번호가 다르다")
        void failUpdatePasswordPasswordUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);

            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            PasswordUpdate.Request request = new PasswordUpdate.Request(
                    "aleex!1234", "qwer1234!", "qwer77777!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));

            when(passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
                    .thenReturn(false);

            MemberException exception = catchThrowableOfType(
                    () -> memberService.updatePassword(request, memberId, memberDto), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_UNMATCH);
        }

        @Test
        @DisplayName("비밀번호 수정 실패 - 유저를 찾을 수 없음")
        void failUpdatePasswordMemberNotFound() {
            //given
            MemberEntity member = createMember(Role.USER);

            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 1L;

            PasswordUpdate.Request request = new PasswordUpdate.Request(
                    "aleex!1234", "qwer1234!", "qwer77777!");

            //when
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.empty());

            MemberException exception = catchThrowableOfType(
                    () -> memberService.updatePassword(request, memberId, memberDto), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }

        @Test
        @DisplayName("비밀번호 수정 실패 - ID 값을 잘 못 입력했음")
        void failUpdatePasswordIdUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);

            MemberDto memberDto = MemberDto.fromEntity(member);

            Long memberId = 111L;

            PasswordUpdate.Request request = new PasswordUpdate.Request(
                    "aleex!1234", "qwer1234!", "qwer77777!");

            //when

            MemberException exception = catchThrowableOfType(
                    () -> memberService.updatePassword(request, memberId, memberDto), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_ID_UNMATCH);
        }
    }

    @Nested
    @DisplayName("비밀번호 분실")
    class LostPassword{

        private MemberEntity createMember(Role role) {
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, role);

            return member;
        }

        @Test
        @DisplayName("비밀번호 분실 성공")
        void successLostPassword() {
            //given
            MemberEntity member = createMember(Role.USER);

            PasswordLostResponse.Request request = PasswordLostResponse.Request
                    .builder()
                    .email("joons@gmail.com")
                    .name("Je Joon")
                    .phoneNumber("010-1234-1234")
                    .build();

            //when
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.of(member));

            when(memberRepository.save(member))
                    .thenReturn(member);

            PasswordLostResponse.Response response = memberService.lostPassword(request);

            //then
            assertThat(response.getMessage()).isEqualTo(request.getEmail() + MailConstant.TEMP_PASSWORD_SUCCESSFULLY_SENT);
        }

        @Test
        @DisplayName("비밀번호 분실 실패 - 이름 잘 못 입력")
        void faillostPasswordNameUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);

            PasswordLostResponse.Request request = PasswordLostResponse.Request
                    .builder()
                    .email("joons@gmail.com")
                    .name("JayJay")
                    .phoneNumber("010-1234-1234")
                    .build();

            //when
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.of(member));

            MemberException exception = catchThrowableOfType(
                    () -> memberService.lostPassword(request), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NAME_UNMATCH);
        }

        @Test
        @DisplayName("비밀번호 분실 실패 - 핸드폰 번호 잘 못 입력")
        void faillostPasswordPhoneUnmatch() {
            //given
            MemberEntity member = createMember(Role.USER);

            PasswordLostResponse.Request request = PasswordLostResponse.Request
                    .builder()
                    .email("joons@gmail.com")
                    .name("JayJay")
                    .phoneNumber("010-1004-1004")
                    .build();

            //when
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.of(member));

            MemberException exception = catchThrowableOfType(
                    () -> memberService.lostPassword(request), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PHONE_NUMBER_UNMATCH);
        }

        @Test
        @DisplayName("비밀번호 분실 실패 - 이메일로 찾은 유저 정보가 없음")
        void faillostPasswordMemberNotFound() {
            //given
            MemberEntity member = createMember(Role.USER);

            PasswordLostResponse.Request request = PasswordLostResponse.Request
                    .builder()
                    .email("joons@gmail.com")
                    .name("JayJay")
                    .phoneNumber("010-1004-1004")
                    .build();

            //when
            when(memberRepository.findByEmail(request.getEmail()))
                    .thenReturn(Optional.empty());

            MemberException exception = catchThrowableOfType(
                    () -> memberService.lostPassword(request), MemberException.class
            );

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }


    }
}
