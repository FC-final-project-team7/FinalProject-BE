package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AwsErrorResult {

    AWS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "다시 요청해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
