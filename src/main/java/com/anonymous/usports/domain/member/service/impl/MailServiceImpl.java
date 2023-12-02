package com.anonymous.usports.domain.member.service.impl;

import com.anonymous.usports.domain.member.service.MailService;
import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.redis.auth.repository.AuthRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final AuthRedisRepository authRedisRepository;
    private static final String senderEmail = "jejoonproject@gmail.com";

    private static int number;

    private static String[] PASSWORD_UPPER_CHARACTERS = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
            , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private static String[] PASSWORD_LOWER_CHARACTERS = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
            , "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    private static String[] PASSWORD_NUMBERS = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private static String[] PASSWORD_SPECIAL_SYMBOLS = new String[] {
            "@", "$", "!", "%", "*", "?", "&"
    };

    private static int createNumber() {
        number = (int)(Math.random() * (9000)) + 100000;
        return number;
    }

    public MimeMessage createMail(String mail, String value, String title, StringBuilder content) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            StringBuilder body = new StringBuilder();
            body.append("<h1>").append(value).append("<h1>").append(content);

            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject(title);
            message.setText(String.valueOf(body), "UTF-8", "html");
        } catch(MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Override
    public int sendEmailAuthMail(String email) {

        String number = String.valueOf(createNumber());
        String title = MailConstant.MEMBER_EMAIL_AUTH_TITLE;
        StringBuilder content = MailConstant.AUTH_EMAIL_CONTENT;

        MimeMessage message = createMail(email, number, title, content);
        javaMailSender.send(message);

        authRedisRepository.saveEmailAuthNumber(email, String.valueOf(number));

        return Integer.parseInt(number);
    }

    private String createPassword(){
        Random random = new Random();
        String newTempPassword = "";

        for (int i = 0; i < 4; i ++) {
            String upper = PASSWORD_UPPER_CHARACTERS[random.nextInt(26)];
            newTempPassword += upper;
        }

        for (int i = 0; i < 4; i ++) {
            String lower = PASSWORD_LOWER_CHARACTERS[random.nextInt(26)];
            newTempPassword += lower;
        }

        for (int i = 0; i < 4; i ++) {
            String num = PASSWORD_NUMBERS[random.nextInt(10)];
            newTempPassword += num;
        }

        for (int i = 0; i < 2; i ++) {
            String symbol = PASSWORD_SPECIAL_SYMBOLS[random.nextInt(6)];
            newTempPassword += symbol;
        }

        String[] splitPass = newTempPassword.split("");
        List<String> passList = Arrays.asList(splitPass);

        Collections.shuffle(passList);

        return String.join("", passList);
    }

    @Override
    public String sendTempPassword(String email) {

        String tempPassword = createPassword();
        String title = MailConstant.TEMP_PASSWORD_EMAIL_TITLE;
        StringBuilder content = MailConstant.TEMP_PASSWORD_EMAIL_CONTENT;

        MimeMessage message = createMail(email, tempPassword, title, content);
        javaMailSender.send(message);

        return tempPassword;
    }
}
