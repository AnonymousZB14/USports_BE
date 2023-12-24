package com.anonymous.usports.domain.member.service;

public interface MailService {

    void sendEmailAuthMail(String email);

    String sendTempPassword(String email);
}
