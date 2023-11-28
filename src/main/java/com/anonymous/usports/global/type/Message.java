package com.anonymous.usports.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {

    DELETE_SUCCESS("삭제가 성공적으로 되었습니다.");

    private String description;
}
