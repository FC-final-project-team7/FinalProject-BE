package com.aipark.web.dto;

import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.tempAudio.TempAudio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    public static class AudioResponse {
        private String status;
        private String url;
    }
}
