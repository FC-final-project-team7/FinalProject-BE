package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProjectErrorResult {

    PROJECT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "프로젝트를 찾지 못하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
