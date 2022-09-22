package com.aipark.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemberErrorResult {
    NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾지 못하였습니다."),
    BAD_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
