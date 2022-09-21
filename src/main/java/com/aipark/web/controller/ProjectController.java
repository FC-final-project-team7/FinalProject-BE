package com.aipark.web.controller;

import com.aipark.biz.service.ProjectService;
import com.aipark.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService projectService;

    /**
     * 텍스트 형태로 프로젝트 만들 때
     * @RequestBody
     * @return
     */
    @PostMapping("/text")
    public ResponseEntity<ProjectDto.TextResponse> projectText(@RequestBody ProjectDto.TextSaveRequest requestDto){
        return ResponseEntity.ok(projectService.textSave(requestDto));
    }

    /**
     * 음성 업로드로 프로젝트 만들 때
     * @RequestBody audioName(음성 업로드 이름)
     * @return
     */
    @PostMapping("/audio")
    public ResponseEntity<ProjectDto.AudioResponse> projectAudio(@RequestBody ProjectDto.AudioSaveRequest requestDto){
        return ResponseEntity.ok(projectService.audioSave(requestDto));

    }
}
