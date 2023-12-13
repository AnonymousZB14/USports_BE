package com.anonymous.usports.global.exception;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public ChatException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
