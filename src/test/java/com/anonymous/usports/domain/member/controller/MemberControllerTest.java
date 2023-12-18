package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.dto.MemberRegister;
import com.anonymous.usports.domain.member.security.TokenProvider;
import com.anonymous.usports.domain.member.service.MemberService;
import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.type.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("회원 가입")
    class registerMember {

        private MemberRegister.Request request(
                String accountName, String name, String email,
                String password, String phoneNumber, LocalDate birthDate,
                Gender gender, String profileOpen
        ) {
            return MemberRegister.Request.builder()
                    .accountName(accountName)
                    .name(name)
                    .email(email)
                    .password(password)
                    .gender(gender)
                    .profileOpen(profileOpen)
                    .build();
        }

        private MemberRegister.Response response(
                String accountName, String email, boolean profileOpen, String message
        ) {
            return MemberRegister.Response.builder()
                    .accountName(accountName)
                    .email(email)
                    .profileOpen(profileOpen)
                    .message(message)
                    .build();
        }

        @Test
        @DisplayName("회원 가입 성공")
        @WithMockUser
        void successRegisterMember() throws Exception {
        //given
            LocalDate birthDate = LocalDate.parse("1999-11-11", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MemberRegister.Request request = new MemberRegister.Request(
                    "joons", "Je Joon", "joons@gmail.com", "abcd1234!",
                      Gender.MALE, "open"
            );

            MemberRegister.Response response = new MemberRegister.Response(
                    "joons", "joons@gmail.com", true, MailConstant.AUTH_EMAIL_SEND
            );

        //when
            given(memberService.registerMember(any()))
                    .willReturn(response);

        //then
            mockMvc.perform(post("/member/register")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountName").exists())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.profileOpen").exists())
                    .andDo(print());
        }
    }
}
