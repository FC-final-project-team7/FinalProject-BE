package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import lombok.*;

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

        public static TextResponse of(Project project){
            return TextResponse.builder()
                    .text(project.getText())
                    .pitch(project.getPitch())
                    .speed(project.getDurationSilence())
                    .language(project.getLanguage())
                    .sex(project.getSex())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioResponse {
        private String audioName;

        public static AudioResponse of(Project project){
            return AudioResponse.builder()
                    .audioName(project.getAudio())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
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
}
