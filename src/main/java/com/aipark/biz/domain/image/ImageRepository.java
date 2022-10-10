package com.aipark.biz.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    // 아바타 리스트 전달하는 메서드
    @Query("SELECT i From Image i WHERE i.category like 'AVATAR%'")
    List<Image> findImageByCategory();

    Optional<Image> findByImageName(String imageName);

    // 아바타 밸류 전달하는 메서드
    List<Image> findImagesByImageNameStartingWithOrCategoryStartingWith(String condition, String background);
}
