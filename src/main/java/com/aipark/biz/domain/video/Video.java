package com.aipark.biz.domain.video;

import com.aipark.biz.domain.BaseTimeEntity;
import com.aipark.biz.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VIDEO")
@Entity
public class Video extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    private String videoUrl;

    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Video(String videoUrl, String projectName, String thumbnail) {
        this.videoUrl = videoUrl;
        this.projectName = projectName;
        this.thumbnail = thumbnail;
    }

    public static Video createVideo(String videoUrl, String projectName, String thumbnail){
        return Video.builder()
                .videoUrl(videoUrl)
                .projectName(projectName)
                .thumbnail(thumbnail)
                .build();
    }

    public void setMember(Member member){
        if(this.member != null){
            this.member.getVideoList().remove(this);
        }
        this.member = member;
        if(!this.member.getVideoList().contains(this)){
            this.member.addVideo(this);
        }
    }
}
