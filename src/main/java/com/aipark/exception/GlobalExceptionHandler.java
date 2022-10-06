package com.aipark.exception;

import lombok.Builder;
import lombok.Getter;
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

    @ExceptionHandler(PythonException.class)
    public ResponseEntity<ErrorResponse> pythonServerException(PythonException pythonServerException){
        PythonErrorResult errorResult = pythonServerException.getPythonServerErrorResult();
        return ResponseEntity.status(errorResult.getHttpStatus()).body(
                ErrorResponse.builder()
                        .code(errorResult.getHttpStatus().name())
                        .message(errorResult.getMessage())
                        .build());
    }

    @ExceptionHandler(AwsException.class)
    public ResponseEntity<ErrorResponse> awsException(AwsException awsException){
        AwsErrorResult errorResult = awsException.getAwsErrorResult();
        return ResponseEntity.status(errorResult.getHttpStatus()).body(
                ErrorResponse.builder()
                        .code(errorResult.getHttpStatus().name())
                        .message(errorResult.getMessage())
                        .build());
    }

    @ExceptionHandler(TempAudioException.class)
    public ResponseEntity<ErrorResponse> tempAudioException(TempAudioException tempAudioException){
        TempAudioErrorResult errorResult = tempAudioException.getTempAudioErrorResult();
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
