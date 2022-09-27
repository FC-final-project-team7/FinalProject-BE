package com.aipark.biz.service;

import com.aipark.web.dto.PythonServerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PythonServerConnectionService {

    public PythonServerDto.CreateAudioResponse createSentenceAudioFile(String text) throws JsonProcessingException {
        String url = "http://localhost:8000/";
//        String url = "http://localhost:8000/audio";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        PythonServerDto.CreateAudioResponse createAudioResponse = objectMapper.readValue(response.getBody(), PythonServerDto.CreateAudioResponse.class);

        return createAudioResponse;
    }

    public List<String> divideSentence(String text) {
        return Arrays.stream(text.split("[.]")).map(s -> s.concat(".")).collect(Collectors.toList());
    }
}
