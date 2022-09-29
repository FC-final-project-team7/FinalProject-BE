package com.aipark.config;

import com.aipark.biz.service.RedisService;
import com.aipark.config.jwt.JwtAccessDeniedHandler;
import com.aipark.config.jwt.JwtAuthenticationEntryPoint;
import com.aipark.config.jwt.JwtSecurityConfig;
import com.aipark.config.jwt.TokenProvider;
import com.aipark.config.oauth.CustomOAuth2UserService;
import com.aipark.config.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler OAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()    //https만을 사용
                .csrf().disable()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .addFilter(corsFilter)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .authorizeRequests()
                .antMatchers("/auth/**"
                        , "/products"
                        , "/v2/api-docs"
                        , "/swagger-resources/**"
                        , "/swagger-ui.html" //"/swagger-ui/index.html"
                        , "/webjars/**"
                        , "/swagger/**"
                        , "/favicon.ico"
                        ,"/members/check-id"
                ).permitAll()
                .anyRequest().authenticated()

                .and()
                .oauth2Login().loginPage("/test/oauth")
                .successHandler(OAuth2SuccessHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        http.apply(new JwtSecurityConfig(tokenProvider, redisService));


        return http.build();
    }
}
