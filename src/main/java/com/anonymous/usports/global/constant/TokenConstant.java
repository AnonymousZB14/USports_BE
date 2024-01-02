package com.anonymous.usports.global.constant;

public class TokenConstant {

    public static final String BEARER = "Bearer ";

    public static final String REFRESH_TOKEN_PREFIX = "rtk-";

    public static final String ACCESS_TOKEN_PREFIX = "atk-";

    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String EMAIL = "email";

    //1시간  1 * 60 * 60 * 1000L

    public static final Long ACCESS_TOKEN_VALID_TIME = 60 *60 * 60 * 1000L;

    //3일 3 * 24 * 60 * 60L
    public static final Long REFRESH_TOKEN_VALID_TIME = 3 * 24 * 60 * 60 * 1000L;

    public static final String KEY_ROLES = "role";

    public static final String BLACK_LIST = "BlackList";

    public static final String LOGOUT_NOT_SUCCESSFUL = "이미 로그아웃이 되었습니다";

    public static final String LOGOUT_SUCCESSFUL = "로그아웃이 정상적으로 진행되었습니다.";
}

