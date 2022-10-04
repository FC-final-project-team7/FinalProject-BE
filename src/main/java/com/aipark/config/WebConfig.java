package com.aipark.config;

import com.aipark.web.interceptor.CheckMemberInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CheckMemberInterceptor checkMemberInterceptor() {
        return new CheckMemberInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkMemberInterceptor())
                .addPathPatterns("/projects/**")
                .excludePathPatterns("/projects", "/projects/text", "/projects/audio", "/projects/avatar");
    }
}
