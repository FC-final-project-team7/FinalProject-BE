package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PythonServerDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAudioRequest {
        private String username;
        private String narration;
        private String text;
        private Long projectId;

        public void setText(String text){
            this.text = text;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SentenceAndUrl {
        private String sentence;
        private String url;

        public ProjectDto.Sentence createSentence() {
            return ProjectDto.Sentence.builder()
                    .sentence(sentence)
                    .sentenceAudio(url)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PythonResponse {
        private String status;
        private String url;

        public void insertData(String status, String url) {
            this.status = status;
            this.url = url;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoRequest{
        private String username;
        private String audioName;
        private String avatar;
        private String background;
        private String projectName;
        private Boolean isAudio;

        public static VideoRequest of(Project project){
            return VideoRequest.builder()
                    .username(project.getMember().getUsername())
                    .audioName(changeAudio(project.getAudio_uuid()))
                    .avatar(project.getAvatar())
                    .background(project.getBackground())
                    .projectName(project.getProjectName())
                    .isAudio(project.getIsAudio())
                    .build();
        }
        // temp.wav -> temp
        public static String changeAudio(String audioName){
            String[] str = audioName.split("[/]");
            String result = str[str.length-1].split("[.]")[0];
            return result;
        }
    }
}
