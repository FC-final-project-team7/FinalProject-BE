package com.aipark.web.controller;

import com.aipark.biz.service.ProjectService;
import com.aipark.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ProjectDto.TextResponse> projectText(){
        return ResponseEntity.ok(projectService.textSave());
    }

    @PostMapping("/auto")
    public void projectUpdate(@RequestBody ProjectDto.BasicDto requestDto){
        projectService.textAutoSave(requestDto);
    }

    /**
     * 음성 업로드로 프로젝트 만들 때
     * @RequestBody audioName(음성 업로드 이름)
     * @return
     */
    @PostMapping("/audio")
    public ResponseEntity<ProjectDto.AudioResponse> projectAudio(){
        return ResponseEntity.ok(projectService.audioSave());

    }

    /**
     * 프로젝트 리스트에서 프로젝트 하나를 요청할 때 사용한다.
     * @RequestBody project_id(프로젝트 기본키 값)
     * @return
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto.BasicDto> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    /**
     * 프로젝트 리스트에서 프로젝트 하나를 삭제할 때 사용한다.
     * @RequestBody project_id(프로젝트 기본키 값)
     * @return
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
