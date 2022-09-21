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
    public static class TextSaveRequest {
        private String text;
        private Long pitch;
        private Long speed;
        private Long durationSilence;
        private String language;
        private String sex;

        public Project toEntity(){
            return Project.builder()
                    .text(text)
                    .pitch(pitch)
                    .speed(speed)
                    .durationSilence(durationSilence)
                    .language(language)
                    .sex(sex)
                    .isAudio(false)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class AudioSaveRequest {
        private String audioName;

        public Project toEntity(){
            return Project.builder()
                    .text("음성업로드입니다")
                    .pitch(-99L)
                    .speed(-99L)
                    .durationSilence(-99L)
                    .language("음성업로드입니다")
                    .sex("음성업로드입니다.")
                    .audioName(audioName)
                    .isAudio(true)
                    .build();
        }
    }
}
