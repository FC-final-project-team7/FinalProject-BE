package com.aipark.biz.service;

import com.aipark.biz.domain.image.ImageRepository;
import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.biz.domain.tempAudio.TempAudio;
import com.aipark.biz.domain.tempAudio.TempAudioRepository;
import com.aipark.biz.service.file.FileStore;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.*;
import com.aipark.web.dto.ProjectDto;
import com.aipark.web.dto.PythonServerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final FileStore fileStore;
    private final PythonService pythonService;
    private final ImageRepository imageRepository;
    private final TempAudioRepository tempAudioRepository;


    @Transactional
    public ProjectDto.TextResponse textSave() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        if (member.getProjectList().size() == 5) {
            List<Project> projects = projectRepository.findAllAsc(member);
            member.getProjectList().remove(projects.get(0));
            projectRepository.delete(projects.get(0));
        }
        Project project = Project.defaultCreate_text();
        member.addProject(project);

        projectRepository.save(project);
        return ProjectDto.TextResponse.of(project);
    }

    @Transactional
    public void textAutoSave(ProjectDto.ProjectAutoRequest requestDto) {
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        project.updateProject(requestDto);
    }

    @Transactional
    public ProjectDto.AudioResponse audioSave(ProjectDto.AudioRequest audioRequest) throws IOException {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        
        MultipartFile audioFile = audioRequest.getAudioFile();
        ProjectDto.UploadFileDto uploadFileDto = fileStore.storeFile(audioFile);
        // 클라이언트로부터 프로젝트 ID 값을 받아와서 DB 에서 프로젝트를 조회하고
        // 해당하는 프로젝트의 audio 와 audio_uuid 에 값을 넣어줌
        Project project = Project.defaultCreate_audio(uploadFileDto.getUploadFileName(), uploadFileDto.getStoreFileName());
        member.addProject(project);

        Project save = projectRepository.save(project);
        return ProjectDto.AudioResponse.of(save);

    }
    @Transactional(readOnly = true)
    public ProjectDto.BasicDto getProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        if (!checkMember(project.getMember().getUsername())) {
            throw new MemberException(MemberErrorResult.MEMBER_INCORRECT);
        }

        return project.createBasicDto();
    }

    @Transactional
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    /**
     * 로그인한 사용자의 프로젝트에 접근했는지 체크해주는 메소드
     *
     * @param projectUsername
     * @return
     */
    //TODO intercepter로 바꾸기
    @Transactional(readOnly = true)
    public boolean checkMember(String projectUsername) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        return member.getUsername().equals(projectUsername);
    }
    public List<ProjectDto.BasicDto> getProjectList() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectDto.BasicDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 수정페이지로 넘어오면 문장별 음성 생성 요청을 파이썬 서버에 보낸다.
     * 그리고 받은 문장별 음성 파일의 주소를 테이블에 저장한다.
     *
     * @param requestDto
     * @return ProjectDto.ModificationPageResponse
     */
    @Transactional
    public ProjectDto.ModificationPageResponse textModificationPage(ProjectDto.ProjectAutoRequest requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        PythonServerDto.CreateAudioRequest request = requestDto.toCreateAudioRequest(member.getUsername());

        ProjectDto.ModificationPageResponse response = pythonService.createSentenceAudioFile(request);

        return response;
    }

    /**
     * 수정페이지에서 자동저장 api가 오면 전체 텍스트만 업데이트 해준다.
     *
     * @param requestDto
     */
    @Transactional
    public void projectTextAutoSave(ProjectDto.TextAndUrlDto requestDto) {
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));
        project.textUpdateProject(requestDto);
    }

    /**
     * 텍스트로 음성 파일 생성 요청을 파이썬에 보내고, 받은 파일명과 주소를 project에 저장한다.
     *
     * @param requestDto
     * @return ProjectDto.AvatarPage
     */
    @Transactional
    public ProjectDto.AvatarPageResponse moveAvatarPage(ProjectDto.TextAndUrlDto requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        PythonServerDto.AudioResponse responseDto = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        project.updateProjectAudioUrl(responseDto);

        ProjectDto.AvatarPageResponse avatarPageResponseDto = project.createAvatarPageDto();

        return avatarPageResponseDto;
    }

    /**
     * 음성생성 요청시, 기존의 파일을 삭제하고 새로운 파일을 저장한다.
     *
     * @param requestDto
     * @return ProjectDto.TextAndUrlDto
     */
    @Transactional
    public ProjectDto.TextAndUrlDto makeAudioFile(ProjectDto.TextAndUrlDto requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        PythonServerDto.AudioResponse audioFile = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        fileStore.deleteFile(project.getAudio_uuid());

        //project에 이름과 파일명 업데이트
        project.updateProjectAudioUrl(audioFile);

        return requestDto.of(audioFile.getUrl());
    }

    // 아바타 리스트 전달
    @Transactional(readOnly = true)
    public List<ProjectDto.ImageDto> sendAvatar() {
        return imageRepository.findImageByCategory().stream()
                .map(ProjectDto.ImageDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProjectDto.ValueDto> sendValue(ProjectDto.AvatarRequest avatarRequest) {
        Project project = projectRepository.findById(avatarRequest.getProjectId()).orElseThrow();
        project.setAvatar(avatarRequest.getImageName());

        String avatar = avatarRequest.getImageName();
        String substring = avatar.substring(6);
        return imageRepository.findImagesByImageNameStartingWithOrCategoryStartingWith(substring,"BACKGROUND").stream()
                .map(ProjectDto.ValueDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 문장별 음성 파일 생성 요청 시(생성 버튼 눌렀을 때)
     * s3에 저장되어있는 파일은 삭제하고, tempAudio 테이블의 주소를 바꿔준다.
     * @param requestDto
     * @return ProjectDto.TextAndUrlDto
     */
    @Transactional
    public ProjectDto.TextAndUrlDto makeAudioBySentence(ProjectDto.TextAndUrlDto requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        TempAudio tempAudio = tempAudioRepository.findByTempUrl(requestDto.getAudioUrl()).orElseThrow(
                () -> new TempAudioException(TempAudioErrorResult.TEMP_AUDIO_NOT_FOUND));

        PythonServerDto.AudioResponse response = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        fileStore.deleteFile(requestDto.getAudioUrl());

        tempAudio.updateTempUrl(response.getUrl());

        return requestDto.of(response.getUrl());
    }
}
