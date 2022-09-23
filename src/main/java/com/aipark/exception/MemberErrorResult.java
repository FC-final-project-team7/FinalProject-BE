package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorResult {
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "회원을 찾지 못하였습니다."),
    BAD_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
