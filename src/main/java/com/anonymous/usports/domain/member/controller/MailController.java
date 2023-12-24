package com.anonymous.usports.domain.member.controller;

import com.anonymous.usports.domain.member.service.MailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "메일(Mail)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MailController {

    private final MailService mailService;

    /**
     * 메일 보내기
     * @param email
     * @return 인증 번호
     */
    @PostMapping("/mail/{email}")
    public String MailSend(
            @PathVariable("email") String email
    ){
        mailService.sendEmailAuthMail(email);;

        return "test sent";
    }

}