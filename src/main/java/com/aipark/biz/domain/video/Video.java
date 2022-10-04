package com.aipark.biz.domain.video;

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
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Video(String videoUrl, Member member) {
        this.videoUrl = videoUrl;
        this.member = member;
    }

    public static Video createVideo(String videoUrl){
        return Video.builder().videoUrl(videoUrl).build();
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
