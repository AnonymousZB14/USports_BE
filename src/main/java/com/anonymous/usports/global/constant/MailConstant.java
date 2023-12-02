package com.anonymous.usports.global.constant;

public class MailConstant {

    public static final String MEMBER_EMAIL_AUTH_TITLE = "USports 회원 이메일 인증";

    public static final String AUTH_EMAIL_SEND = "이메일로 회원 인증 번호를 보냈습니다";

    public static final String PREFIX_AUTH = "auth-";

    public static final Long AUTH_EMAIL_VALID_TIME = 10L;

    public static final StringBuilder AUTH_EMAIL_CONTENT = new StringBuilder()
            .append("\n<h3>요청하신 USports 회원 인증 번호입니다<h3>")
            .append("<h3>로그인 후, 10분 이내로 인증 부탁드립니다<h3>")
            .append("<h3>인증 후 추가 회원 설정을 부탁드립니다!<h3>")
            .append("<h3>감사합니다!<h3>");

    public static final String TEMP_PASSWORD_EMAIL_TITLE = "USports에서 임시 비밀번호를 보내드립니다";
    public static final StringBuilder TEMP_PASSWORD_EMAIL_CONTENT = new StringBuilder()
            .append("\n<h3>임시 비밀번호입니다<h3>")
            .append("<h3>계속 사용하실 수 있지만, 개인 페이지에 들어가서 비밀번호를 바꾸는 것을 추천해드립니다<h3>")
            .append("<h3>감사합니다!<h3>");

    public static final String TEMP_PASSWORD_SUCCESSFULLY_SENT = " 에, 성공적으로 임시 비밀번호가 보내졌습니다";
}
