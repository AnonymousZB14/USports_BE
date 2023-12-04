package com.anonymous.usports.domain.member.service;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.entity.InterestedSportsEntity;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.InterestedSportsRepository;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.service.impl.MailServiceImpl;
import com.anonymous.usports.domain.member.service.impl.MemberServiceImpl;
import com.anonymous.usports.domain.sports.entity.SportsEntity;
import com.anonymous.usports.domain.sports.repository.SportsRepository;
import com.anonymous.usports.global.constant.MailConstant;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

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

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

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

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

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
                    .thenReturn(true);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.registerMember(request), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("회원가입 실패 - 닉네임이 이미 있음")
        void failAccountAlreadyExist() {
            //given
            LocalDate birthDate = LocalDate.parse("1996-02-17", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberEntity member = member(1L, "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                    "010-1234-1234", birthDate, Gender.MALE, null, null, null, null, null,
                    true, Role.UNAUTH);

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
                    .thenReturn(true);

            MemberException exception =
                    catchThrowableOfType(() ->
                            memberService.registerMember(request), MemberException.class);

            //then
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
    }

}
