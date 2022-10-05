package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum TempAudioErrorResult {

    TEMP_AUDIO_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "음성파일을 찾지 못하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
