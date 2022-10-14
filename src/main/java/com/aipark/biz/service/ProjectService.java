package com.aipark.biz.service;

import com.aipark.biz.domain.image.Image;
import com.aipark.biz.domain.image.ImageRepository;
import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.biz.domain.tempAudio.TempAudio;
import com.aipark.biz.domain.tempAudio.TempAudioRepository;
import com.aipark.biz.domain.video.Video;
import com.aipark.biz.domain.video.VideoRepository;
import com.aipark.biz.service.file.FileStore;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.*;
import com.aipark.web.dto.ProjectDto;
import com.aipark.web.dto.PythonServerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final FileStore fileStore;
    private final PythonService pythonService;
    private final ImageRepository imageRepository;
    private final TempAudioRepository tempAudioRepository;
    private final VideoRepository videoRepository;


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

        if (member.getProjectList().size() == 5) {
            List<Project> projects = projectRepository.findAllAsc(member);
            member.getProjectList().remove(projects.get(0));
            projectRepository.delete(projects.get(0));
        }

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

        return project.createBasicDto();
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        if (!project.getAudio_uuid().isEmpty()) {
            // project에 저장되어있는 음성파일 삭제
            if (project.getIsAudio()) {
                fileStore.deleteFileByAudio(project.getAudio_uuid(), member.getUsername());
            }
            if (!project.getIsAudio()) {
                fileStore.deleteFileByText(project.getAudio_uuid());
            }
            // tempAudio에 저장되어있는 음성파일들 삭제
            List<TempAudio> tempAudioList = tempAudioRepository.findAllByProject(project);
            if (tempAudioList.size() != 0) {
                for (TempAudio tempAudio : tempAudioList) {
                    fileStore.deleteFileByText(tempAudio.getTempUrl());
                }
            }
        }

        // 테이블에 있는 데이터 삭제
        tempAudioRepository.deleteAllByProject(project);
        projectRepository.deleteById(projectId);
    }

    public List<ProjectDto.BasicDto> getProjectList() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        return projectRepository.findAllAsc(member)
                .stream()
                .map(project -> new ProjectDto.BasicDto(project, imageRepository.findByImageName(project.getAvatar()).orElse(Image.createImage())))
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
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));
        // 받은 reqeustDto를 python server에 필요한 request로 변환하는 작업
        PythonServerDto.CreateAudioRequest request = requestDto.toCreateAudioRequest(member.getUsername());

        // 전체 text로 음성파일을 만들고, project에 저장하는 작업
        PythonServerDto.PythonResponse audioFile = pythonService.createAudioFile(request);
        project.updateProjectAudioUrl(audioFile);

        // 전체 text 나누기
        List<String> sentence = divideSentence(request.getText());
        ProjectDto.ModificationPageResponse mpr = ProjectDto.ModificationPageResponse.of(request);
        for (String s : sentence) {
            PythonServerDto.CreateAudioRequest car = requestDto.toCreateAudioRequest(member.getUsername());
            car.setText(s);
            PythonServerDto.PythonResponse sentenceAudio = pythonService.createAudioFile(car);

            ProjectDto.Sentence sen = ProjectDto.Sentence.of(s, sentenceAudio.getUrl());
            tempAudioRepository.save(TempAudio.builder().project(project).tempUrl(sentenceAudio.getUrl()).build());
            mpr.setSentenceList(sen);
        }
        mpr.addAudio(audioFile.getUrl());

        return mpr;
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
    public ProjectDto.AvatarPageDto moveAvatarPage(ProjectDto.TextAndUrlDto requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(requestDto.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        PythonServerDto.PythonResponse responseDto = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        project.updateProjectAudioUrl(responseDto);

        ProjectDto.AvatarPageDto avatarPageResponseDto = project.createAvatarPageDto();

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

        PythonServerDto.PythonResponse audioFile = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        fileStore.deleteFileByText(project.getAudio_uuid());

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
        return imageRepository.findImagesByImageNameStartingWithOrCategoryStartingWith(substring, "BACKGROUND").stream()
                .map(ProjectDto.ValueDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 입력받은 value 및 배경 저장
     *
     * @param selectedAvatarValue
     */
    @Transactional
    public void avatarAutoSave(ProjectDto.AvatarPageDto selectedAvatarValue) {
        Project project = projectRepository.findById(selectedAvatarValue.getProjectId()).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));
        project.setCategories(selectedAvatarValue);
    }

    /**
     * 문장별 음성 파일 생성 요청 시(생성 버튼 눌렀을 때)
     * s3에 저장되어있는 파일은 삭제하고, tempAudio 테이블의 주소를 바꿔준다.
     *
     * @param requestDto
     * @return ProjectDto.TextAndUrlDto
     */
    @Transactional
    public ProjectDto.TextAndUrlDto makeAudioBySentence(ProjectDto.TextAndUrlDto requestDto) {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        TempAudio tempAudio = tempAudioRepository.findByTempUrl(requestDto.getAudioUrl()).orElseThrow(
                () -> new TempAudioException(TempAudioErrorResult.TEMP_AUDIO_NOT_FOUND));

        PythonServerDto.PythonResponse response = pythonService.createAudioFile(requestDto.toCreateAudioRequest(member.getUsername()));

        fileStore.deleteFileByText(requestDto.getAudioUrl());

        tempAudio.updateTempUrl(response.getUrl());

        return requestDto.of(response.getUrl());
    }

    @Transactional
    public void completedProject(ProjectDto.AvatarPageDto avatarPageRequestDto) {
        // 아바타 페이지 자동저장
        // Dto 생성
        Project project = projectRepository.findById((avatarPageRequestDto.getProjectId())).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        // request Dto 생성
        PythonServerDto.VideoRequest videoRequestDto = PythonServerDto.VideoRequest.of(project);

        // 비디오 파일 생성
        PythonServerDto.PythonResponse videoFile = pythonService.createVideoFile(videoRequestDto);

        // thumbnail을 위한 이미지 불러오기
        String thumbnail = "https://jeongsu-aipark.s3.ap-northeast-2.amazonaws.com/black.png";
        Image imageName = imageRepository.findByImageName(project.getAvatar()).orElse(null);
        if (imageName != null) {
            thumbnail = imageName.getImageUrl();
        }

        // url에 비디오 생성
        Video video = Video.createVideo(videoFile.getUrl(), project.getProjectName(), thumbnail);

        // member에 video 저장
        project.getMember().addVideo(video);

        // video 저장
        videoRepository.save(video);

        // tempAudio에 저장되어있는 음성파일들 삭제
        List<TempAudio> tempAudioList = tempAudioRepository.findAllByProject(project);
        if (!tempAudioList.isEmpty()) {
            for (TempAudio tempAudio : tempAudioList) {
                fileStore.deleteFileByText(tempAudio.getTempUrl());
            }
        }
        // tempAudio 삭제
        tempAudioRepository.deleteAllByProject(project);
    }

    // 영상 리스트
    public List<ProjectDto.VideoListResponse> getVideoList() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        return videoRepository.findAllByMember(member)
                .stream()
                .map(ProjectDto.VideoListResponse::new)
                .collect(Collectors.toList());
    }

    // 영상 조회
    @Transactional(readOnly = true)
    public ProjectDto.VideoResponse getVideo(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.VIDEO_NOT_FOUND));

        return ProjectDto.VideoResponse.of(video);
    }

    /**
     * 문장을 나눠주는 메소드
     *
     * @param text
     * @return
     */
    public List<String> divideSentence(String text) {
        return Arrays.stream(text.split("\n")).collect(Collectors.toList());
    }
}
