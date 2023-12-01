package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.service.MailService;
import com.anonymous.usports.global.constant.EmailConstant;
import com.anonymous.usports.global.redis.auth.repository.AuthRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final AuthRedisRepository authRedisRepository;
    private static final String senderEmail = "jejoonproject@gmail.com";

    private static int number;

    private static int createNumber() {
        number = (int)(Math.random() * (9000)) + 100000;
        return number;
    }

    public MimeMessage createMail(String mail) {
        int number = createNumber();

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject(EmailConstant.MEMBER_TITLE);
            String body = "";
            body += "<h3>" + "요청하신 USports 회원 인증 번호입니다" + "<h3>";
            body += "<h3>" + number + "<h3>";
            body += "<h3>" + "인증 후 추가 회원 설정을 부탁드립니다!" + "<h3>";
            body += "<h3>" + "감사합니다!" + "<h3>";
            message.setText(body, "UTF-8", "html");
        } catch(MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Override
    public int sendEmailAuthMail(String email) {

        MimeMessage message = createMail(email);
        javaMailSender.send(message);

        authRedisRepository.saveEmailAuthNumber(email, String.valueOf(number));

        return number;
    }
}
