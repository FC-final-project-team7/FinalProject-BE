package com.aipark.biz.service;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.config.SecurityUtil;
import com.aipark.web.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ProjectDto.TextResponse textSave() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

        Project project = Project.defaultCreate();
        member.addProject(project);

        projectRepository.save(project);
        return ProjectDto.TextResponse.of(project);
    }

    @Transactional
    public void textAutoSave(ProjectDto.BasicDto requestDto){
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new RuntimeException("해당 프로젝트를 찾지 못하였습니다"));

        project.updateProject(requestDto);
    }

    @Transactional
    public ProjectDto.AudioResponse audioSave() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

        Project project = Project.builder().isAudio(true).build();
        member.addProject(project);

        projectRepository.save(project);

        return ProjectDto.AudioResponse.of(project);
    }

    @Transactional(readOnly = true)
    public ProjectDto.BasicDto getProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 프로젝트가 없습니다."));

        boolean isMember = checkMember(project.getMember().getUsername());

        if (!isMember) {
            throw new RuntimeException("잘못된 접근입니다.");
        }

        return project.createBasicDto();
    }

    @Transactional(readOnly = true)
    public boolean checkMember(String projectUsername) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

        return member.getUsername().equals(projectUsername);
    }
}
