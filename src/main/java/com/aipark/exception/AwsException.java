package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AwsException extends RuntimeException{
    private final AwsErrorResult awsErrorResult;
}
