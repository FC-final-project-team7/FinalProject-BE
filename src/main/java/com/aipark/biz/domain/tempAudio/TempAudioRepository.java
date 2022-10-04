package com.aipark.biz.domain.tempAudio;

import com.aipark.biz.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempAudioRepository extends JpaRepository<TempAudio, Long> {

    Optional<TempAudio> findByTempUrl(String tempUrl);

    void deleteAllByProject(Project project);
}
