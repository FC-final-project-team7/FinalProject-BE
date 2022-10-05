package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.tempAudio.TempAudio;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
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
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAudioResponse {
        private String status;
        private List<SentenceAndUrl> url;

        public static TempAudio toEntity(Project project, String url) {
            return TempAudio.builder()
                    .project(project)
                    .tempUrl(url)
                    .build();
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
            log.info("UUID 분리 ㅣ: {}", result);
            return result;
        }
    }
}
