package com.aipark.web.controller;

import com.aipark.biz.service.ProjectService;
import com.aipark.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 텍스트 입력에서 자동저장
     * @RequestBody 자동저장을 위한 데이터(
     *         projectId        : 프로젝트 고유 id
     *         projectName      : 프로젝트 이름
     *         avatarAudio      : 아바타 음성
     *         sex              : 성별
     *         language         : 언어
     *         durationSilence  : 간격
     *         pitch            : 음성 톤
     *         speed            : 음성 속도
     *         text             : 텍스트
     *         audio            :
     *         isAudio;         : 음성 업로드 유/무
     * @return projectDto
     */
    @PostMapping("/auto")
    public ResponseEntity<ProjectDto.TextResponse> projectAutoUpdate(@RequestBody ProjectDto.ProjectAutoRequest requestDto){
        return ResponseEntity.ok(projectService.textAutoSave(requestDto));
    }
    @GetMapping
    public ResponseEntity<List<ProjectDto.BasicDto>> getProjectList(){
        return ResponseEntity.ok(projectService.getProjectList());
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
