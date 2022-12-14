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
        // ???????????????????????? ???????????? ID ?????? ???????????? DB ?????? ??????????????? ????????????
        // ???????????? ??????????????? audio ??? audio_uuid ??? ?????? ?????????
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
            // project??? ?????????????????? ???????????? ??????
            if (project.getIsAudio()) {
                fileStore.deleteFileByAudio(project.getAudio_uuid(), member.getUsername());
            }
            if (!project.getIsAudio()) {
                fileStore.deleteFileByText(project.getAudio_uuid());
            }
            // tempAudio??? ?????????????????? ??????????????? ??????
            List<TempAudio> tempAudioList = tempAudioRepository.findAllByProject(project);
            if (tempAudioList.size() != 0) {
                for (TempAudio tempAudio : tempAudioList) {
                    fileStore.deleteFileByText(tempAudio.getTempUrl());
                }
            }
        }

        // ???????????? ?????? ????????? ??????
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
     * ?????????????????? ???????????? ????????? ?????? ?????? ????????? ????????? ????????? ?????????.
     * ????????? ?????? ????????? ?????? ????????? ????????? ???????????? ????????????.
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
        // ?????? reqeustDto??? python server??? ????????? request??? ???????????? ??????
        PythonServerDto.CreateAudioRequest request = requestDto.toCreateAudioRequest(member.getUsername());

        // ?????? text??? ??????????????? ?????????, project??? ???????????? ??????
        PythonServerDto.PythonResponse audioFile = pythonService.createAudioFile(request);
        project.updateProjectAudioUrl(audioFile);

        // ?????? text ?????????
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
     * ????????????????????? ???????????? api??? ?????? ?????? ???????????? ???????????? ?????????.
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
     * ???????????? ?????? ?????? ?????? ????????? ???????????? ?????????, ?????? ???????????? ????????? project??? ????????????.
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
     * ???????????? ?????????, ????????? ????????? ???????????? ????????? ????????? ????????????.
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

        //project??? ????????? ????????? ????????????
        project.updateProjectAudioUrl(audioFile);

        return requestDto.of(audioFile.getUrl());
    }

    // ????????? ????????? ??????
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
     * ???????????? value ??? ?????? ??????
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
     * ????????? ?????? ?????? ?????? ?????? ???(?????? ?????? ????????? ???)
     * s3??? ?????????????????? ????????? ????????????, tempAudio ???????????? ????????? ????????????.
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
        // ????????? ????????? ????????????
        // Dto ??????
        Project project = projectRepository.findById((avatarPageRequestDto.getProjectId())).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

        // request Dto ??????
        PythonServerDto.VideoRequest videoRequestDto = PythonServerDto.VideoRequest.of(project);

        // ????????? ?????? ??????
        PythonServerDto.PythonResponse videoFile = pythonService.createVideoFile(videoRequestDto);

        // thumbnail??? ?????? ????????? ????????????
        String thumbnail = "https://jeongsu-aipark.s3.ap-northeast-2.amazonaws.com/black.png";
        Image imageName = imageRepository.findByImageName(project.getAvatar()).orElse(null);
        if (imageName != null) {
            thumbnail = imageName.getImageUrl();
        }

        // url??? ????????? ??????
        Video video = Video.createVideo(videoFile.getUrl(), project.getProjectName(), thumbnail);

        // member??? video ??????
        project.getMember().addVideo(video);

        // video ??????
        videoRepository.save(video);

        // tempAudio??? ?????????????????? ??????????????? ??????
        List<TempAudio> tempAudioList = tempAudioRepository.findAllByProject(project);
        if (!tempAudioList.isEmpty()) {
            for (TempAudio tempAudio : tempAudioList) {
                fileStore.deleteFileByText(tempAudio.getTempUrl());
            }
        }
        // tempAudio ??????
        tempAudioRepository.deleteAllByProject(project);
    }

    // ?????? ?????????
    public List<ProjectDto.VideoListResponse> getVideoList() {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        return videoRepository.findAllByMember(member)
                .stream()
                .map(ProjectDto.VideoListResponse::new)
                .collect(Collectors.toList());
    }

    // ?????? ??????
    @Transactional(readOnly = true)
    public ProjectDto.VideoResponse getVideo(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new ProjectException(ProjectErrorResult.VIDEO_NOT_FOUND));

        return ProjectDto.VideoResponse.of(video);
    }

    /**
     * ????????? ???????????? ?????????
     *
     * @param text
     * @return
     */
    public List<String> divideSentence(String text) {
        return Arrays.stream(text.split("\n")).collect(Collectors.toList());
    }
}
