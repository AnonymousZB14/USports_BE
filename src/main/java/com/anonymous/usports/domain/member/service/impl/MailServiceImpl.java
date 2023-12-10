package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.service.MailService;
import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.constant.RandomCreator;
import com.anonymous.usports.global.redis.auth.repository.AuthRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final AuthRedisRepository authRedisRepository;
    private static final String senderEmail = "jejoonproject@gmail.com";

    public MimeMessage createMail(String mail, String value, String title, StringBuilder content) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            StringBuilder body = new StringBuilder();
            body.append("<h1>").append(value).append("<h1>").append(content);

            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject(title);
            message.setText(String.valueOf(body), "UTF-8", "html");

        } catch(MessagingException e) {
            log.info("catch");
            e.printStackTrace();
        }

        return message;
    }

    @Override
    @Async
    public void sendEmailAuthMail(String email) {

        String number = String.valueOf(RandomCreator.createNumber());
        String title = MailConstant.MEMBER_EMAIL_AUTH_TITLE;
        StringBuilder content = MailConstant.AUTH_EMAIL_CONTENT;

        sendEmail(email, number, title, content);

        authRedisRepository.saveEmailAuthNumber(email, number);
    }

    @Async
    protected void sendEmail(String email, String tempPassword, String title, StringBuilder content){
        MimeMessage message = createMail(email, tempPassword, title, content);
        javaMailSender.send(message);
    };

    @Override
    public String sendTempPassword(String email) {

        String tempPassword = RandomCreator.createPassword();
        String title = MailConstant.TEMP_PASSWORD_EMAIL_TITLE;
        StringBuilder content = MailConstant.TEMP_PASSWORD_EMAIL_CONTENT;

        sendEmail(email, tempPassword, title, content);

        return tempPassword;
    }
}
