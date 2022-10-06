package com.aipark.biz.domain.member;

import com.aipark.biz.domain.BaseTimeEntity;
import com.aipark.biz.domain.enums.Authority;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.video.Video;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name ="MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Video> videoList = new ArrayList<>();

    @Builder
    public Member(String username, String email, String password, String name, String phoneNumber, Authority authority) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.authority = authority;
    }

    public void addProject(Project project) {
        this.projectList.add(project);
        if(project.getMember() != this) {
            project.setMember(this);
        }
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void addVideo(Video video) {
        this.videoList.add(video);
        if(video.getMember() != this){
            video.setMember(this);
        }
    }
}

