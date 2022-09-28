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
        private List<SentenceAndUrl> sentenceAndUrl;

        public static TempAudio toEntity(Project project, String sentence) {
            return TempAudio.builder()
                    .project(project)
                    .tempUrl(sentence)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SentenceAndUrl {
        private String sentence;
        private String url;
    }

}
