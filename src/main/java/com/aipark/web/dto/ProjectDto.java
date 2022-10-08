package com.aipark.web.dto;

import com.aipark.biz.domain.image.Image;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.video.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectAutoRequest {
        private Long projectId;
        private String projectName;
        private String avatarAudio;
        private String sex;
        private String language;
        private Double durationSilence;
        private Double pitch;
        private Double speed;
        private String text;
        private String audio;
        private Boolean isAudio;

        public PythonServerDto.CreateAudioRequest toCreateAudioRequest(String username) {
            return PythonServerDto.CreateAudioRequest.builder()
                    .username(username)
                    .narration("none")
                    .text(text)
                    .projectId(projectId)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextResponse {
        private Long projectId;
        private String text;
        private Double pitch;
        private Double speed;
        private Double durationSilence;
        private String language;
        private String sex;
        private String avatarAudio;
        private LocalDateTime modifiedDate;

        public static TextResponse of(Project project) {
            return TextResponse.builder()
                    .projectId(project.getId())
                    .text(project.getText())
                    .pitch(project.getPitch())
                    .speed(project.getSpeed())
                    .durationSilence(project.getDurationSilence())
                    .language(project.getLanguage())
                    .sex(project.getSex())
                    .avatarAudio(project.getAvatarAudio())
                    .modifiedDate(project.getModifiedDate())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioResponse {
        private Long projectId;

        public static AudioResponse of(Project project) {
            return AudioResponse.builder()
                    .projectId(project.getId())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BasicDto {
        private Long projectId;
        private String projectName;
        private String avatarAudio;
        private String sex;
        private String language;
        private Double durationSilence;
        private Double pitch;
        private Double speed;
        private String text;
        private String audio;
        private Boolean isAudio;
        private String avatar;
        private String category1;
        private String category2;
        private String category3;
        private String background;
        private LocalDateTime modifiedDate;

        @Builder
        public BasicDto(Project project) {
            this.projectId = project.getId();
            this.projectName = project.getProjectName();
            this.avatarAudio = project.getAvatarAudio();
            this.sex = project.getSex();
            this.language = project.getLanguage();
            this.durationSilence = project.getDurationSilence();
            this.pitch = project.getPitch();
            this.speed = project.getSpeed();
            this.text = project.getText();
            this.audio = project.getAudio();
            this.isAudio = project.getIsAudio();
            this.avatar = project.getAvatar();
            this.category1 = project.getCategory1();
            this.category2 = project.getCategory2();
            this.category3 = project.getCategory3();
            this.background = project.getBackground();
            this.modifiedDate = project.getModifiedDate();
        }

        public Project toEntity() {
            return Project.builder()
                    .projectName(projectName)
                    .avatarAudio(avatarAudio)
                    .sex(sex)
                    .language(language)
                    .durationSilence(durationSilence)
                    .pitch(pitch)
                    .speed(speed)
                    .text(text)
                    .audio(audio)
                    .isAudio(isAudio)
                    .avatar(avatar)
                    .category1(category1)
                    .category2(category2)
                    .category3(category3)
                    .background(background)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class AudioRequest {
        private MultipartFile audioFile;
    }

    @Getter
    @AllArgsConstructor
    public static class UploadFileDto {
        // 유저가 업로드하는 파일명
        private String uploadFileName;
        // S3 에 저장될 파일명
        private String storeFileName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ModificationPageResponse {
        private Long projectId;
        private String text;
        private String audio;
        private List<Sentence> sentenceList;

        public static ModificationPageResponse of(PythonServerDto.CreateAudioRequest request) {
            return ModificationPageResponse.builder()
                    .projectId(request.getProjectId())
                    .text(request.getText())
                    .audio("")
                    .sentenceList(new ArrayList<>())
                    .build();
        }

        public void setSentenceList(ProjectDto.Sentence sentence) {
            sentenceList.add(sentence);
        }
    }

    @Getter
    @Builder
    public static class Sentence {
        private String sentence;
        private String sentenceAudio;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TextAndUrlDto {
        private Long projectId;
        private String text;
        private String audioUrl;

        public PythonServerDto.CreateAudioRequest toCreateAudioRequest(String username) {
            return PythonServerDto.CreateAudioRequest.builder()
                    .username(username)
                    .narration("none")
                    .text(text)
                    .projectId(projectId)
                    .build();
        }

        public ProjectDto.TextAndUrlDto of(String url) {
            return TextAndUrlDto.builder()
                    .projectId(projectId)
                    .text(text)
                    .audioUrl(url)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AvatarPageDto {
        private Long projectId;
        private String avatar;
        private String category1;
        private String category2;
        private String category3;
        private String background;
    }

    // avatar 리스트 전달
    @Getter
    public static class ImageDto {
        private String imageName;
        private String imageUrl;

        @Builder
        public ImageDto(Image image) {
            this.imageName = image.getCategory();
            this.imageUrl = image.getImageUrl();
        }
    }

    @Getter
    public static class ValueDto {
        private String category;
        private String imageUrl;

        @Builder
        public ValueDto(Image image) {
            this.category = image.getCategory();
            this.imageUrl = image.getImageUrl();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class AvatarRequest {
        private Long projectId;
        private String imageName;
    }

    @Getter
    @AllArgsConstructor
    public static class SelectedAvatarValue {
        private Long projectId;
        private String category1;
        private String category2;
        private String category3;
        private String background;
    }

    @Getter
    @NoArgsConstructor
    public static class VideoListResponse{
        private Long id;
        private String name;
        private Boolean generated;
        private LocalDateTime createdDate;
        private String thumbnail;

        public VideoListResponse(Video video) {
            this.id = video.getId();
            this.name = video.getProjectName();
            this.generated = true;
            this.createdDate = video.getCreatedDate();
            this.thumbnail = video.getThumbnail();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoResponse{
        private String projectName;
        private String videoAddress;

        public static VideoResponse of(Video video){
            return VideoResponse
                    .builder()
                    .projectName(video.getProjectName())
                    .videoAddress(video.getVideoUrl())
                    .build();
        }
    }
}
