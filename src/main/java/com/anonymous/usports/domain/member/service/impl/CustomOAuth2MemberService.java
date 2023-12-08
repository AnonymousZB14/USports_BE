package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.dto.MemberDto;
import com.anonymous.usports.domain.member.entity.MemberEntity;
import com.anonymous.usports.domain.member.repository.MemberRepository;
import com.anonymous.usports.domain.member.security.oauth2.KakaoMemberInfo;
import com.anonymous.usports.domain.member.security.oauth2.OAuth2MemberInfo;
import com.anonymous.usports.global.type.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("안녕");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("{}", oAuth2User);

        // 서비스 구분을 위한 작업 (여기서 어느 서비스와 연동되는지 나옴. Kakao 또는 naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("{}", registrationId);

        OAuth2MemberInfo memberInfo = checkResourceServer(registrationId, oAuth2User);
        log.info("{}", memberInfo);

        String provider = memberInfo.getProvider();
        String providerId = memberInfo.getProviderId();
        String name = memberInfo.getName();
        String nickName = memberInfo.getNickName();
        String email = memberInfo.getEmail();
        String profileImg = memberInfo.getProfileImage();
        String birthDay = memberInfo.getBirthDay();
        String gender = memberInfo.getGender();

        Optional<MemberEntity> member = memberRepository.findByEmail(email);

        MemberEntity memberEntity = null;

        if (member.isEmpty()) {
            log.info("{} : 새로운 유저입니다", email);

            memberEntity = memberRepository.save(
                    MemberEntity.builder()
                            .accountName(nickName)
                            .name(name)
                            .email(email)
                            .password(passwordEncoder.encode("abcd1234!")) // todo : 바꾸기
                            .birthDate(LocalDate.now()) // todo : 바꾸기
                            .gender(Gender.BOTH) //todo : 바꾸기
                            .profileOpen(false)
                            .build());
        } else {
            memberEntity = member.get();
        }

        MemberDto memberDto = MemberDto.fromEntity(memberEntity);
        memberDto.setAttributes(oAuth2User.getAttributes());

        return memberDto;
    }

    // 자원을 가지고 오는 서버 (kakao? naver?)
    private OAuth2MemberInfo checkResourceServer(String registrationId, OAuth2User oAuth2User) {

        OAuth2MemberInfo memberInfo = null;

        if (registrationId.equals("kakao")) {
            memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
            log.info("{}",oAuth2User.getAttributes());
        }

        return memberInfo;
    }
}
