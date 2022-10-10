package com.aipark.biz.domain.image;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "IMAGE")
@Getter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long id;

    @Column(name = "CATEGORY")
    private String category;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Builder
    public Image(String category, String imageName, String imageUrl) {
        this.category = category;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public static Image createImage() {
        return Image.builder()
                .category("black")
                .imageName("black")
                .imageUrl("https://jeongsu-aipark.s3.ap-northeast-2.amazonaws.com/black.png")
                .build();
    }
}
