package com.aipark.web.controller;

import com.aipark.biz.service.ProjectService;
import com.aipark.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public void projectUpdate(@RequestBody ProjectDto.ProjectAutoRequest requestDto){
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
    @PostMapping("/edit/auto")
    public ResponseEntity<String> projectAutoUpdate(@RequestBody ProjectDto.ProjectAutoRequest requestDto){
        return ResponseEntity.ok("수정됐습니다.");
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto.BasicDto>> getProjectList(){
        return ResponseEntity.ok(projectService.getProjectList());
    }

    /**
     * 음성 업로드로 프로젝트 만들 때
     * @ModelAttribute projectId(프로젝트 id)
     * @return
     */
    @PostMapping("/audio")
    public ResponseEntity<ProjectDto.AudioResponse> projectAudio(@ModelAttribute ProjectDto.AudioRequest audioRequest) throws IOException {
        return ResponseEntity.ok(projectService.audioSave(audioRequest));
    }

    /**
     * 프로젝트 리스트에서 프로젝트 하나를 요청할 때
     * @PathVariable project_id(프로젝트 기본키 값)
     * @return
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto.BasicDto> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    /**
     * 프로젝트 리스트에서 프로젝트 하나를 삭제할 때
     * @PathVariable project_id(프로젝트 기본키 값)
     * @return "삭제됐습니다."
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("삭제됐습니다.");
    }

    /**
     * 텍스트 입력 페이지에서 텍스트 수정 페이지로 넘어갈 때
     * @RequestBody
     * @return
     */
    @PutMapping("/edit")
    public ResponseEntity<ProjectDto.ModificationPageResponse> modifyText(@RequestBody ProjectDto.ProjectAutoRequest requestDto) {
        projectService.textAutoSave(requestDto);
        return ResponseEntity.ok(projectService.textModificationPage(requestDto));
    }

    /**
     * 수정페이지에서 자동저장할 때
     * @RequestBody 자동저장을 위한 데이터(projectId, text)
     * @return "수정됐습니다."
     */
    @PutMapping("/edit/auto")
    public ResponseEntity<String> ModificationPageAutoSave(@RequestBody ProjectDto.TextAndUrlDto requestDto) {
        projectService.projectTextAutoSave(requestDto);
        return ResponseEntity.ok("수정됐습니다.");
    }

    /**
     * 아바타 선택 페이지로 넘어갈 때
     * @param requestDto
     * @return ProjectDto.AvatarPage
     */
    @PutMapping("/edit/audio")
    public ResponseEntity<ProjectDto.AvatarPageDto> moveAvatarPage(@RequestBody ProjectDto.TextAndUrlDto requestDto) {
        projectService.projectTextAutoSave(requestDto);
        return ResponseEntity.ok(projectService.moveAvatarPage(requestDto));
    }

    /**
     * 아바타 리스트 보내준다.
     * @return imageName(이미지 이름), imageUrl(s3에 저장되어 있는 이미지 경로)
     */
    @GetMapping("/avatar")
    public ResponseEntity<List<ProjectDto.ImageDto>> sendAvatar() {
        return ResponseEntity.ok(projectService.sendAvatar());
    }

    /**
     * 아바타 value 요청 시(아바타 리스트 선택 하였을 때)
     * @param avatarRequest
     * @return ProjectDto.ValueDto 리스트
     */
    @PutMapping("/avatar")
    public ResponseEntity<List<ProjectDto.ValueDto>> sendValue(@RequestBody ProjectDto.AvatarRequest avatarRequest) {
        return ResponseEntity.ok(projectService.sendValue(avatarRequest));
    }

    /**
<<<<<<< HEAD
=======
     * 아바타 선택 페이지 자동 저장 요청 시
     * @param selectedAvatarValue
     * @return "프로젝트 자동 저장 완료"
     */
    @PutMapping("/avatar/auto")
    public ResponseEntity<String> selectValue(@RequestBody ProjectDto.AvatarPageDto selectedAvatarValue) {
        projectService.avatarAutoSave(selectedAvatarValue);
        return ResponseEntity.ok("프로젝트가 자동 저장 완료되었습니다.");
    }
    /**
>>>>>>> feat-project
     * 음성 생성 요청 시(문장별 음성 생성 제외)
     * @param requestDto
     * @return ProjectDto.TextAndUrlDto
     */
    @PostMapping("/audio/text")
    public ResponseEntity<ProjectDto.TextAndUrlDto> makeAudioFile(@RequestBody ProjectDto.TextAndUrlDto requestDto) {
        return ResponseEntity.ok(projectService.makeAudioFile(requestDto));
    }

    /**
     * 문장별 음성 파일 생성 요청 시(생성 버튼 눌렀을 때)
     * @param requestDto
     * @return ProjectDto.TextAndUrlDto
     */
    @PostMapping("/audio/sentence")
    public ResponseEntity<ProjectDto.TextAndUrlDto> makeAudioBySentence(@RequestBody ProjectDto.TextAndUrlDto requestDto) {
        return ResponseEntity.ok(projectService.makeAudioBySentence(requestDto));
    }

    /**
     * 프로젝트 완성
     */
    @PutMapping("/avatar/category")
    public ResponseEntity<String> completedProject(@RequestBody ProjectDto.AvatarPageDto avatarPageRequestDto){
        projectService.avatarAutoSave(avatarPageRequestDto);
        projectService.completedProject(avatarPageRequestDto);

        return ResponseEntity.ok("프로젝트가 완성되었습니다.");
    }

    @GetMapping("/videos")
    public ResponseEntity<List<ProjectDto.VideoListResponse>> getVideoList(){
        return ResponseEntity.ok(projectService.getVideoList());
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<ProjectDto.VideoResponse> getVideo(@PathVariable("videoId") Long videoId){
        return ResponseEntity.ok(projectService.getVideo(videoId));
    }
}
