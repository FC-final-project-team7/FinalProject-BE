package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class ProjectDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectAutoRequest{
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
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextResponse {
        private String text;
        private Double pitch;
        private Double speed;
        private Double durationSilence;
        private String language;
        private String sex;
        private String avatarAudio;

        public static TextResponse of(Project project){
            return TextResponse.builder()
                    .text(project.getText())
                    .pitch(project.getPitch())
                    .speed(project.getSpeed())
                    .durationSilence(project.getDurationSilence())
                    .language(project.getLanguage())
                    .sex(project.getSex())
                    .avatarAudio(project.getAvatarAudio())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioResponse {
        private Long projectId;

        public static AudioResponse of(Project project){
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
        }

        public Project toEntity(){
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
}
