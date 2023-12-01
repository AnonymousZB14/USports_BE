package com.anonymous.usports.domain.member.service;

public interface MailService {

    int sendEmailAuthMail(String email);

    String sendTempPassword(String email);
}
