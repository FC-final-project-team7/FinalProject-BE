package com.aipark.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TempAudioException extends RuntimeException{
    private final TempAudioErrorResult tempAudioErrorResult;
}
