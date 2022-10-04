package com.aipark.biz.service;

import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.biz.domain.tempAudio.TempAudioRepository;
import com.aipark.exception.ProjectErrorResult;
import com.aipark.exception.ProjectException;
import com.aipark.exception.PythonErrorResult;
import com.aipark.exception.PythonException;
import com.aipark.web.dto.ProjectDto;
import com.aipark.web.dto.PythonServerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PythonService {

    private final TempAudioRepository tempAudioRepository;
    private final ProjectRepository projectRepository;


    public PythonServerDto.PythonResponse createVideoFile(PythonServerDto.VideoRequest request) {
        // Http 통신 body에 들어갈 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", request.getUsername());
        jsonObject.put("audio_name", request.getAudioName());
        jsonObject.put("avatar", request.getAvatar());
        jsonObject.put("background", request.getBackground());
        jsonObject.put("project_name", request.getProjectName());

        // webClient를 사용하여 서버간 통신
        PythonServerDto.PythonResponse response = buildWebClient().post()
                .uri("/video")
                .body(Mono.just(jsonObject.toString()), JSONObject.class)
                .retrieve()
                .bodyToMono(PythonServerDto.PythonResponse.class)
                .block();

        // 파이썬 서버에서 제대로 생성이 안되면 fail이 들어온다.
        if (response.getStatus().equals("fail")) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }

        // 오디오들을 임시 음성 테이블에 저장한다.
        return response;
    }
    /**
     * 입력 페이지에서 수정 페이지로 넘어갈 때, 파이썬에 문장별 음성 파일 생성 요청할 때,
     * @param request
     * @return
     */
    public ProjectDto.ModificationPageResponse createSentenceAudioFile(PythonServerDto.CreateAudioRequest request) {

        List<String> sentences = divideSentence(request.getText());

        // Http 통신 body에 들어갈 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", request.getUsername());
        jsonObject.put("text", sentences);
        jsonObject.put("narration", request.getNarration());

        // webClient를 사용하여 서버간 통신
        PythonServerDto.CreateAudioResponse response = buildWebClient().post()
                .uri("/audios/sentence")
                .body(Mono.just(jsonObject.toString()), JSONObject.class)
                .retrieve()
                .bodyToMono(PythonServerDto.CreateAudioResponse.class)
                .block();

        // 파이썬 서버에서 제대로 생성이 안되면 fail이 들어온다.
        if (response.getStatus().equals("fail")) {
            throw new PythonException(PythonErrorResult.AUDIO_CREATE_ERROR);
        }

        // 오디오들을 임시 음성 테이블에 저장한다.
        saveTempAudio(response, request.getProjectId());

        return createDto(request, response);
    }

    /**
     * 수정 페이지에서 아바타 선택 페이지로 넘어갈 때, 파이썬에 음성 파일 생성 요청할 때
     * 생성버튼을 눌러서 음성을 요청할 때
     * @param requestDto
     * @return
     */
    public PythonServerDto.PythonResponse createAudioFile(PythonServerDto.CreateAudioRequest requestDto) {

        // Http 통신 body에 들어갈 json 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", requestDto.getUsername());
        jsonObject.put("text", requestDto.getText());
        jsonObject.put("narration", "none");

        // webClient를 사용하여 서버간 통신
        PythonServerDto.PythonResponse responseDto = buildWebClient().post()
                .uri("/audios")
                .body(Mono.just(jsonObject.toString()), JSONObject.class)
                .retrieve()
                .bodyToMono(PythonServerDto.PythonResponse.class)
                .block();

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
                .baseUrl("http://localhost:8000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 문장을 나눠주는 메소드
     * @param text
     * @return
     */
    public List<String> divideSentence(String text) {
        return Arrays.stream(text.split("[.]")).map(s -> s.concat(".")).collect(Collectors.toList());
    }

    /**
     * 문장별 음성들을 임시로 저장하는 메소드
     * @param response
     * @param projectId
     */
    public void saveTempAudio(PythonServerDto.CreateAudioResponse response, Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        for (PythonServerDto.SentenceAndUrl s : response.getUrl()) {
            tempAudioRepository.save(PythonServerDto.CreateAudioResponse.toEntity(project, s.getUrl()));
        }
    }

    /**
     * ModificationPageResponse Dto로 변경해주는 메소드
     * @param request
     * @param response
     * @return
     */
    public ProjectDto.ModificationPageResponse createDto(PythonServerDto.CreateAudioRequest request, PythonServerDto.CreateAudioResponse response) {
        ProjectDto.ModificationPageResponse mpr = ProjectDto.ModificationPageResponse.of(request);

        for (PythonServerDto.SentenceAndUrl s : response.getUrl()) {
            ProjectDto.Sentence sentence = s.createSentence();
            mpr.setSentenceList(sentence);
        }
        return mpr;
    }
}
