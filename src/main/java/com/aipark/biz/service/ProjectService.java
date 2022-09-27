package com.aipark.biz.service;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.*;
import com.aipark.web.dto.ProjectDto;
import com.aipark.web.dto.PythonServerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final PythonServerConnectionService pythonServerConnectionService;

    @Transactional
    public ProjectDto.TextResponse textSave() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        if(member.getProjectList().size()==5){
            List<Project> projects = projectRepository.findAllAsc(member);
            member.getProjectList().remove(projects.get(0));
            projectRepository.delete(projects.get(0));
        }
        Project project = Project.defaultCreate();
        member.addProject(project);

        projectRepository.save(project);
        return ProjectDto.TextResponse.of(project);
    }

    @Transactional
    public ProjectDto.TextResponse textAutoSave(ProjectDto.ProjectAutoRequest requestDto){
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        project.updateProject(requestDto);

        return ProjectDto.TextResponse.of(project);
    }

    @Transactional
    public ProjectDto.AudioResponse audioSave() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        Project project = Project.builder().isAudio(true).build();
        member.addProject(project);

        projectRepository.save(project);

        return ProjectDto.AudioResponse.of(project);
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

    public PythonServerDto.CreateAudioResponse TextModificationPage(String text) {
        PythonServerDto.CreateAudioResponse response;

        try {
            response = pythonServerConnectionService.createSentenceAudioFile(text);
        } catch (JsonProcessingException e) {
            throw new PythonServerException(PythonServerErrorResult.JSON_MAPPING_ERROR);
        }

        return response;
    }
}
