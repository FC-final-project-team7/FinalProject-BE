package com.aipark.biz.service;

import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.biz.domain.tempAudio.TempAudioRepository;
import com.aipark.exception.ProjectErrorResult;
import com.aipark.exception.ProjectException;
import com.aipark.exception.PythonServerErrorResult;
import com.aipark.exception.PythonServerException;
import com.aipark.web.dto.ProjectDto;
import com.aipark.web.dto.PythonServerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PythonServerService {

    private final TempAudioRepository tempAudioRepository;
    private final ProjectRepository projectRepository;

    public ProjectDto.ModificationPageResponse createSentenceAudioFile(PythonServerDto.CreateAudioRequest request) {
        String url = "http://localhost:8000/audios/sentence";
        List<String> sentences = divideSentence(request.getText());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", request.getUsername());
        jsonObject.put("text", sentences);
        jsonObject.put("narration", request.getNarration());

        HttpEntity<String> requestMessage = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<PythonServerDto.CreateAudioResponse> responseEntity = restTemplate.postForEntity(url, requestMessage, PythonServerDto.CreateAudioResponse.class);

        PythonServerDto.CreateAudioResponse response = responseEntity.getBody();

        if (response.getStatus().equals("fail")) {
            throw new PythonServerException(PythonServerErrorResult.AUDIO_CREATE_ERROR);
        }

        saveTempAudio(response, request.getProjectId());

        return createDto(request, response);
    }

    public List<String> divideSentence(String text) {
        return Arrays.stream(text.split("[.]")).map(s -> s.concat(".")).collect(Collectors.toList());
    }

    @Transactional
    public void saveTempAudio(PythonServerDto.CreateAudioResponse response, Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        for (PythonServerDto.SentenceAndUrl s : response.getUrl()) {
            tempAudioRepository.save(PythonServerDto.CreateAudioResponse.toEntity(project, s.getUrl()));
        }
    }

    public ProjectDto.ModificationPageResponse createDto(PythonServerDto.CreateAudioRequest request, PythonServerDto.CreateAudioResponse response) {
        ProjectDto.ModificationPageResponse mpr = ProjectDto.ModificationPageResponse.of(request);

        for (PythonServerDto.SentenceAndUrl s : response.getUrl()) {
            ProjectDto.Sentence sentence = s.createSentence();
            mpr.setSentenceList(sentence);
        }
        return mpr;
    }
}
