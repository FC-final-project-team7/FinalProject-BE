package com.aipark.biz.service;

import com.aipark.biz.domain.image.ImageRepository;
import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.biz.service.file.FileStore;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.MemberErrorResult;
import com.aipark.exception.MemberException;
import com.aipark.exception.ProjectErrorResult;
import com.aipark.exception.ProjectException;
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
    private final PythonServerService pythonServerService;
    private final ImageRepository imageRepository;


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

    @Transactional
    public ProjectDto.ModificationPageResponse textModificationPage(ProjectDto.ProjectAutoRequest requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        PythonServerDto.CreateAudioRequest request = requestDto.toCreateAudioRequest(member.getUsername());

        ProjectDto.ModificationPageResponse response = pythonServerService.createSentenceAudioFile(request);

        return response;
    }

    @Transactional
    public void projectTextAutoSave(ProjectDto.TextAutoSave requestDto) {
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));
        project.textUpdateProject(requestDto);
    }

    @Transactional
    public ProjectDto.AvatarPage moveAvatarPage(ProjectDto.TextAutoSave requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        PythonServerDto.CreateAudioRequest createAudioRequest = requestDto.toCreateAudioRequest(member.getUsername());
        PythonServerDto.AudioResponse responseDto = pythonServerService.createAudioFile(createAudioRequest);

        project.updateProjectAudioUrl(responseDto);
        ProjectDto.AvatarPage avatarPageDto = project.createAvatarPageDto();

        return avatarPageDto;
    }

    // 아바타 리스트 전달
    @Transactional(readOnly = true)
    public List<ProjectDto.ImageDto> sendAvatar() {
        return imageRepository.findImageByCategory().stream()
                .map(ProjectDto.ImageDto::new)
                .collect(Collectors.toList());
    }
}
