package com.aipark.biz.domain.project;

import com.aipark.biz.domain.member.Member;
import com.aipark.web.dto.ProjectDto;
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
    @Column(name = "project_id")
    private Long id;

    private String projectName;
    private String avatarAudio;
    private String sex;
    private String language;
    private Double durationSilence;
    private Double pitch;
    private Double speed;
    private String text;
    private String audio;
    private Boolean isAudio;
    private String avatar;
    private String category1;
    private String category2;
    private String category3;
    private String background;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(Long id,String projectName, String avatarAudio, String sex, String language, Double durationSilence,
                   Double pitch, Double speed, String text, String audio, boolean isAudio, String avatar,
                   String category1, String category2, String category3, String background, Member member) {
        this.id = id;
        this.projectName = projectName;
        this.avatarAudio = avatarAudio;
        this.sex = sex;
        this.language = language;
        this.durationSilence = durationSilence;
        this.pitch = pitch;
        this.speed = speed;
        this.text = text;
        this.audio = audio;
        this.isAudio = isAudio;
        this.avatar = avatar;
        this.category1 = category1;
        this.category2 = category2;
        this.category3 = category3;
        this.background = background;
        this.member = member;
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

    public static Project defaultCreate(){
        return Project.builder()
                .projectName("")
                .avatarAudio("")
                .sex("MALE")
                .language("korean")
                .durationSilence(0.5)
                .pitch(1.0)
                .speed(1.0)
                .text("")
                .audio("")
                .isAudio(false)
                .avatar("")
                .category1("")
                .category2("")
                .category3("")
                .background("")
                .build();

    }

    public void updateProject(ProjectDto.BasicDto requestDto) {
        this.projectName = requestDto.getProjectName();
        this.avatarAudio = requestDto.getAvatarAudio();
        this.sex = requestDto.getSex();
        this.language = requestDto.getLanguage();
        this.durationSilence = requestDto.getDurationSilence();
        this.pitch = requestDto.getPitch();
        this.speed = requestDto.getSpeed();
        this.text = requestDto.getText();
        this.audio = requestDto.getAudio();
        this.isAudio = requestDto.getIsAudio();
        this.avatar = requestDto.getAvatar();
        this.category1 = requestDto.getCategory1();
        this.category2 = requestDto.getCategory2();
        this.category3 = requestDto.getCategory3();
        this.background = requestDto.getBackground();
    }

    public ProjectDto.BasicDto createBasicDto() {
        return ProjectDto.BasicDto.builder()
                .projectId(id)
                .projectName(projectName)
                .avatarAudio(avatarAudio)
                .sex(sex)
                .language(language)
                .durationSilence(durationSilence)
                .pitch(pitch)
                .speed(speed)
                .text(text)
                .audio(audio)
                .isAudio(isAudio)
                .avatar(avatar)
                .category1(category1)
                .category2(category2)
                .category3(category3)
                .background(background)
                .build();
    }
}
