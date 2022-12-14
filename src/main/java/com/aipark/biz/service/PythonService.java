package com.aipark.biz.service;

import com.aipark.exception.PythonErrorResult;
import com.aipark.exception.PythonException;
import com.aipark.web.dto.PythonServerDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class PythonService {

    public PythonServerDto.PythonResponse createVideoFile(PythonServerDto.VideoRequest request) {
        CountDownLatch latch = new CountDownLatch(1);
        PythonServerDto.PythonResponse responseDto = new PythonServerDto.PythonResponse();
        // Http 통신 body에 들어갈 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", request.getUsername());
        jsonObject.put("audio_name", request.getAudioName());
        jsonObject.put("avatar", request.getAvatar());
        jsonObject.put("background", request.getBackground());
        jsonObject.put("project_name", request.getProjectName());
        jsonObject.put("is_audio", request.getIsAudio());

        // webClient를 사용하여 서버간 통신
        buildWebClient().post()
                .uri("/video")
                .body(Mono.just(jsonObject.toString()), JSONObject.class)
                .retrieve()
                .bodyToMono(PythonServerDto.PythonResponse.class)
                .subscribe(response -> {
                    responseDto.insertData(response.getStatus(), response.getUrl());
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }
        // 파이썬 서버에서 제대로 생성이 안되면 fail이 들어온다.
        if (responseDto.getStatus().equals("fail")) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }

        // 오디오들을 임시 음성 테이블에 저장한다.
        return responseDto;
    }

    /**
     * 수정 페이지에서 아바타 선택 페이지로 넘어갈 때, 파이썬에 음성 파일 생성 요청할 때
     * 생성버튼을 눌러서 음성을 요청할 때
     * @param requestDto
     * @return
     */
    public PythonServerDto.PythonResponse createAudioFile(PythonServerDto.CreateAudioRequest requestDto) {
        CountDownLatch latch = new CountDownLatch(1);
        PythonServerDto.PythonResponse responseDto = new PythonServerDto.PythonResponse();
        // Http 통신 body에 들어갈 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", requestDto.getUsername());
        jsonObject.put("text", requestDto.getText());
        jsonObject.put("narration", "none");

        // webClient를 사용하여 서버간 통신
        buildWebClient().post()
                .uri("/audios")
                .body(Mono.just(jsonObject.toString()), JSONObject.class)
                .retrieve()
                .bodyToMono(PythonServerDto.PythonResponse.class)
                .subscribe(response -> {
                    responseDto.insertData(response.getStatus(), response.getUrl());
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }
        // 파이썬 서버에서 제대로 생성이 안되면 fail이 들어온다.
        if (responseDto.getStatus().equals("fail")) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }

        return responseDto;
    }

    /**
     * WebClient 기본 설정 메소드
     * @return WebClient
     */
    public WebClient buildWebClient() {
        return WebClient.builder()
                .baseUrl("http://jsl:8000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
