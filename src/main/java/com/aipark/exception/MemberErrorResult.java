package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorResult {

    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "너를 찾지 못하였습니다."),
    BAD_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
    MEMBER_INCORRECT(HttpStatus.FORBIDDEN, "너의 프로젝트가 아닙니다");
    AUTH_FAIL(HttpStatus.BAD_REQUEST, "인증 실패");


    private final HttpStatus httpStatus;
    private final String message;

}
