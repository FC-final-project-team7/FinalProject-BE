package com.aipark.biz.domain.project;

import com.aipark.biz.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.member = :member ORDER BY p.id ASC")
    List<Project> findAllAsc(@Param("member") Member member);
}
