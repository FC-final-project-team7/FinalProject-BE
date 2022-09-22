package com.aipark.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberException extends RuntimeException{
    private final MemberErrorResult memberErrorResult;

}
