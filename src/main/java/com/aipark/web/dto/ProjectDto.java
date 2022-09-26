package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import lombok.*;

public class ProjectDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TextResponse {
        private String text;
        private Long pitch;
        private Long speed;
        private Long durationSilence;
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
                    .audioName(project.getAudioName())
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
        private String avatarAudioName;
        private String sex;
        private String language;
        private Long durationSilence;
        private Long pitch;
        private Long speed;
        private String text;
        private String audioName;
        private boolean isAudio;

        public Project toEntity(){
            return Project.builder()
                    .text(text)
                    .pitch(pitch)
                    .speed(speed)
                    .durationSilence(durationSilence)
                    .language(language)
                    .sex(sex)
                    .audioName(audioName)
                    .isAudio(isAudio)
                    .build();
        }
    }
}
