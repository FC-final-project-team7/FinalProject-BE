package com.aipark.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> memberException(MemberException memberException){
        MemberErrorResult errorResult = memberException.getMemberErrorResult();
        return ResponseEntity.status(errorResult.getHttpStatus()).body(
                ErrorResponse.builder()
                        .code(errorResult.getHttpStatus().name())
                        .message(errorResult.getMessage())
                        .build());
    }

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ErrorResponse> projectException(ProjectException projectException){
        ProjectErrorResult errorResult = projectException.getProjectErrorResult();
        return ResponseEntity.status(errorResult.getHttpStatus()).body(
                ErrorResponse.builder()
                        .code(errorResult.getHttpStatus().name())
                        .message(errorResult.getMessage())
                        .build());
    }

    @ExceptionHandler(PythonServerException.class)
    public ResponseEntity<ErrorResponse> pythonServerException(PythonServerException pythonServerException){
        PythonServerErrorResult errorResult = pythonServerException.getPythonServerErrorResult();
        return ResponseEntity.status(errorResult.getHttpStatus()).body(
                ErrorResponse.builder()
                        .code(errorResult.getHttpStatus().name())
                        .message(errorResult.getMessage())
                        .build());
    }

    @Getter
    @Builder
    static class ErrorResponse{
        private String code;
        private String message;
    }
}
