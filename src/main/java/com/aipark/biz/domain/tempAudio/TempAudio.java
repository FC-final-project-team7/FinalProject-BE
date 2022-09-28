package com.aipark.biz.domain.tempAudio;

import com.aipark.biz.domain.project.Project;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name ="TEMP_AUDIO")
@Entity
public class TempAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temp_id")
    private Long id;

    private String tempUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public TempAudio(Long id, String tempUrl, Project project) {
        this.id = id;
        this.tempUrl = tempUrl;
        this.project = project;
    }
}
