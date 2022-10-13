package com.aipark.web.interceptor;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.domain.project.Project;
import com.aipark.biz.domain.project.ProjectRepository;
import com.aipark.config.SecurityUtil;
import com.aipark.exception.MemberErrorResult;
import com.aipark.exception.MemberException;
import com.aipark.exception.ProjectErrorResult;
import com.aipark.exception.ProjectException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CheckMemberInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Member member = memberRepository.findByUsername(SecurityUtil.getCurrentMemberName()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        if (request.getHeader("content-type") == null || !request.getHeader("content-type").equals("application/json")) {
            Map attribute = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Long pathVariable = Long.parseLong((String) attribute.get("projectId"));
            Project project = projectRepository.findById((Long) pathVariable).orElseThrow(
                    () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));
            if (member.getId() != project.getMember().getId()) {
                throw new MemberException(MemberErrorResult.MEMBER_INCORRECT);
            } else {
                return true;
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            Map<String, Object> map = objectMapper.readValue(messageBody, new TypeReference<Map<String, Object>>(){});
            Number temp = (Number) map.get("projectId");
            Long projectId = temp.longValue();

            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new ProjectException(ProjectErrorResult.PROJECT_NOT_FOUND));

            if (member.getId() != project.getMember().getId()) {
                throw new MemberException(MemberErrorResult.MEMBER_INCORRECT);
            } else {
                return true;
            }
        }
    }
}
