package com.aipark.biz.domain.project;

import com.aipark.biz.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PROJECT")
@Getter
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatarName;
    private String sex;
    private String language;
    private Long durationSilence;
    private Long pitch;
    private Long speed;
    private String text;
    private String audioName;
    private boolean isAudio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(String avatarName, String sex, String language, Long durationSilence, Long pitch, Long speed, String text, String audioName, boolean isAudio) {
        this.avatarName = avatarName;
        this.sex = sex;
        this.language = language;
        this.durationSilence = durationSilence;
        this.pitch = pitch;
        this.speed = speed;
        this.text = text;
        this.audioName = audioName;
        this.isAudio = isAudio;
    }

    public void setMember(Member member) {
        if(this.member != null){
            this.member.getProjectList().remove(this);
        }
        this.member = member;
        if(!this.member.getProjectList().contains(this)){
            this.member.addProject(this);
        }
    }
}
