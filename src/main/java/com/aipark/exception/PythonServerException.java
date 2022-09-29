package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PythonServerException extends RuntimeException{
    private final PythonServerErrorResult pythonServerErrorResult;
}
