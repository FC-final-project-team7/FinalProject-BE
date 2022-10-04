package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PythonException extends RuntimeException{
    private final PythonErrorResult pythonServerErrorResult;
}
