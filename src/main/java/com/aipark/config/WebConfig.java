package com.aipark.config;

import com.aipark.web.filter.CustomServletWrappingFilter;
import com.aipark.web.interceptor.CheckMemberInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

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
    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CustomServletWrappingFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/projects/auto");
        filterRegistrationBean.addUrlPatterns("/projects/{projectId}");
        filterRegistrationBean.addUrlPatterns("/projects/edit");
        filterRegistrationBean.addUrlPatterns("/projects/edit/auto");
        filterRegistrationBean.addUrlPatterns("/projects/edit/audio");
        filterRegistrationBean.addUrlPatterns("/projects/avatar/auto");
        filterRegistrationBean.addUrlPatterns("/projects/avatar/category");
        filterRegistrationBean.addUrlPatterns("/projects/avatar/text");
        filterRegistrationBean.addUrlPatterns("/projects/avatar/sentence");
        return filterRegistrationBean;
    }
}
