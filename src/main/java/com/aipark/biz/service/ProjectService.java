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
    public ProjectDto.TextResponse textSave(ProjectDto.TextSaveRequest requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

        Project project = requestDto.toEntity();
        member.addProject(project);

        projectRepository.save(project);
        return ProjectDto.TextResponse.of(project);
    }

    @Transactional
    public ProjectDto.AudioResponse audioSave(ProjectDto.AudioSaveRequest requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 멤버가 없습니다."));

        Project project = requestDto.toEntity();
        member.addProject(project);

        projectRepository.save(project);

        return ProjectDto.AudioResponse.of(project);
    }
}
