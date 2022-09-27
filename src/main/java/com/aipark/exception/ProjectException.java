package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProjectException extends RuntimeException{
    private final ProjectErrorResult projectErrorResult;
}
