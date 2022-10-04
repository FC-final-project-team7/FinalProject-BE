package com.aipark.biz.domain.video;

import com.aipark.biz.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByMember(Member member);
}
